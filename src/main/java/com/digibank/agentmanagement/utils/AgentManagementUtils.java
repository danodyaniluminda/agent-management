package com.digibank.agentmanagement.utils;

import com.digibank.agentmanagement.config.Constants;
import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.AgentStatus;
import com.digibank.agentmanagement.domain.AgentType;
import com.digibank.agentmanagement.domain.packagemanagement.*;
import com.digibank.agentmanagement.exception.*;
import com.digibank.agentmanagement.repository.AgentDetailsRepository;
import com.digibank.agentmanagement.service.AgentDetailsService;
import com.digibank.agentmanagement.service.packagemanagement.*;
import com.digibank.agentmanagement.web.dto.TransferCommissionDto;
import com.digibank.agentmanagement.web.dto.keycloakutil.LoggedInUser;
import com.digibank.agentmanagement.web.dto.packagemanagement.LimitProfileDto;
import com.digibank.agentmanagement.web.dto.packagemanagement.PackageCurrencyLimitDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class AgentManagementUtils {

    @Autowired
    private AgentManagementPropertyHolder agentManagementPropertyHolder;
    @Autowired
    private OperationService operationService;
    @Autowired
    private AssetService assetService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private PackageOperationPermissionService packageOperationPermissionService;
    @Autowired
    private PackageCurrencyLimitService packageCurrencyLimitService;
    @Lazy
    @Autowired
    private AgentLimitProfileService agentLimitProfileService;
    @Autowired
    private AgentDetailsService agentDetailsService;
    @Autowired
    private AgentDetailsRepository agentDetailsRepository;

    public Gson getGson() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.registerTypeAdapter(ApiResponse.class, new ApiErrorResponseDeserializer());
//        gsonBuilder.registerTypeAdapter(ExistingCustomerRegistration.class, new ExistingCustomerRegistrationDeserializer());
//        gsonBuilder.registerTypeAdapter(AccountBalanceResponse.class, new AccountBalanceResponseDeserializer());
//        gsonBuilder.registerTypeAdapter(AccountStatementResponse.class, new AccountStatementResponseDeserializer());
        gsonBuilder.serializeNulls().setPrettyPrinting();
        return gsonBuilder.create();
    }

    public String getTransactionKey(int length) {
        String randomString = new RandomStringGenerator.Builder().build().generate(length).toUpperCase();
        return appendCharactersInKey(randomString);
    }

    private String appendCharactersInKey(String randomString) {
        StringBuilder stringBuilder = new StringBuilder(randomString);
        stringBuilder.insert(4, agentManagementPropertyHolder.getKeySeparatorCharacter());
        stringBuilder.insert(8, agentManagementPropertyHolder.getKeySeparatorCharacter());
        return stringBuilder.toString();
    }

    public static KeycloakAuthenticationToken getKeycloakToken(KeycloakAuthenticationToken token){
        if (token != null) return token;
        else{
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            return (KeycloakAuthenticationToken) request.getUserPrincipal();
//            KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal) keycloakAuthenticationToken.getPrincipal();
//            String token = principal.getKeycloakSecurityContext().getIdTokenString();
        }
    }

    public static String getLoggedInUserUsername(KeycloakAuthenticationToken token) {
        return ((KeycloakPrincipal) token.getPrincipal())
                .getKeycloakSecurityContext()
                .getToken()
                .getPreferredUsername();
    }

    public static String getLoggedInUserUsernameMobileNumber(KeycloakAuthenticationToken token) {
        return ((KeycloakPrincipal) token.getPrincipal())
                .getKeycloakSecurityContext()
                .getToken()
                .getPreferredUsername().split("_")[1];
    }

    public static LoggedInUser getLoggedInUser(KeycloakAuthenticationToken token) {
        AccessToken accessToken =
                ((KeycloakPrincipal) token.getPrincipal()).getKeycloakSecurityContext().getToken();
        return LoggedInUser.builder()
                .username(accessToken.getPreferredUsername())
                .mobileCountryCode(
                        accessToken.getOtherClaims().get(Constants.MOBILE_COUNTRY_CODE) != null
                                ? accessToken.getOtherClaims().get(Constants.MOBILE_COUNTRY_CODE).toString()
                                : null)
                .iamId(loggedInIAMId(token))
                .build();
    }

    public String getBearerTokenString(KeycloakAuthenticationToken token) {
        return AgentManagementConstants.BEARER_TOKEN_TYPE + " " +
                ((KeycloakPrincipal) token.getPrincipal()).getKeycloakSecurityContext().getTokenString();
    }

    public static String loggedInIAMId(KeycloakAuthenticationToken token) {
        return token.getPrincipal().toString();
    }

    public static void isValidId(Long id) {
        if (id < 0) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    public String getUUID() {
        return UUID.randomUUID().toString();
    }

    public void checkTransactionLimit(LocalDate todayDate, BigDecimal currentTransactionAmount, PackageCurrencyLimit packageCurrencyLimit, AgentLimitProfile agentLimitProfile) throws TransactionLimitException {
        checkDailyCountLimit(todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        checkWeeklyCountLimit(todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        checkMonthlyCountLimit(todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);

        checkDailySendingLimit(todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        checkWeeklySendingLimit(todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        checkMonthlySendingLimit(todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
    }

    private void checkDailyCountLimit(LocalDate todayDate, BigDecimal currentTransactionAmount,
                                      PackageCurrencyLimit packageCurrencyLimit, AgentLimitProfile agentLimitProfile) {
        log.info("checking daily limits availability for date: {}, for amount: {}, with packageCurrencyLimit: {} and agentLimitProfile: {}", todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        if (todayDate.isEqual(agentLimitProfile.getDailyCycleDate())) {
            if (agentLimitProfile.getDailyTransactionCount() >= packageCurrencyLimit.getDailyTransactionCount()) {
                throw new TransactionLimitException(ApiError.DAILY_TRANSACTION_COUNT_LIMIT_REACHED);
            }
            if (agentLimitProfile.getDailyTransactionAmount().compareTo(packageCurrencyLimit.getDailyTransactionAmount()) >= 0) {
                throw new TransactionLimitException(ApiError.DAILY_TRANSACTION_AMOUNT_LIMIT_REACHED);
            } else if (agentLimitProfile.getDailyTransactionAmount().add(currentTransactionAmount).compareTo(packageCurrencyLimit.getDailyTransactionAmount()) >= 0) {
                throw new TransactionLimitException(ApiError.DAILY_TRANSACTION_AMOUNT_ABOVE_LIMIT,
                        packageCurrencyLimit.getDailyTransactionAmount().subtract(agentLimitProfile.getDailyTransactionAmount()).toString());
            }
        } else if (todayDate.isAfter(agentLimitProfile.getDailyCycleDate())) {
            agentLimitProfile.setDailyCycleDate(todayDate);
            agentLimitProfile.setDailyTransactionCount(0);
            agentLimitProfile.setDailyTransactionAmount(new BigDecimal("0"));
        } else {
            throw new TransactionLimitException(ApiError.PACKAGE_CURRENCY_NOT_AVAILABLE);
        }
    }

    private void checkWeeklyCountLimit(LocalDate todayDate, BigDecimal currentTransactionAmount,
                                       PackageCurrencyLimit packageCurrencyLimit, AgentLimitProfile agentLimitProfile) {
        log.info("checking weekly limits availability for date: {}, for amount: {}, with packageCurrencyLimit: {} and agentLimitProfile: {}", todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        if (todayDate.isEqual(agentLimitProfile.getWeeklyCycleDate()) || todayDate.isBefore(agentLimitProfile.getWeeklyCycleDate())) {
            if (agentLimitProfile.getWeeklyTransactionCount() >= packageCurrencyLimit.getWeeklyTransactionCount()) {
                throw new TransactionLimitException(ApiError.WEEKLY_TRANSACTION_COUNT_LIMIT_REACHED);
            }
            if (agentLimitProfile.getWeeklyTransactionAmount().compareTo(packageCurrencyLimit.getWeeklyTransactionAmount()) >= 0) {
                throw new TransactionLimitException(ApiError.WEEKLY_TRANSACTION_AMOUNT_LIMIT_REACHED);
            } else if (agentLimitProfile.getWeeklyTransactionAmount().add(currentTransactionAmount)
                    .compareTo(packageCurrencyLimit.getWeeklyTransactionAmount()) >= 0) {
                throw new TransactionLimitException(ApiError.WEEKLY_TRANSACTION_AMOUNT_ABOVE_LIMIT,
                        packageCurrencyLimit.getWeeklyTransactionAmount().subtract(agentLimitProfile.getWeeklyTransactionAmount()).toString());
            }
        } else if (todayDate.isAfter(agentLimitProfile.getDailyCycleDate())) {
            // Week completed yesterday. need to reset date from current date plus 6 days and limits
            agentLimitProfile.setWeeklyCycleDate(todayDate.plusDays(6));
            agentLimitProfile.setWeeklyTransactionCount(0);
            agentLimitProfile.setWeeklyTransactionAmount(new BigDecimal("0"));
        }
    }

    private void checkMonthlyCountLimit(LocalDate todayDate, BigDecimal currentTransactionAmount,
                                        PackageCurrencyLimit packageCurrencyLimit, AgentLimitProfile agentLimitProfile) {
        log.info("checking monthly limits availability for date: {}, for amount: {}, with packageCurrencyLimit: {} and agentLimitProfile: {}", todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        if (todayDate.isEqual(agentLimitProfile.getMonthlyCycleDate()) || todayDate.isBefore(agentLimitProfile.getMonthlyCycleDate())) {
            if (agentLimitProfile.getMonthlyTransactionCount() >= packageCurrencyLimit.getMonthlyTransactionCount()) {
                throw new TransactionLimitException(ApiError.MONTHLY_TRANSACTION_COUNT_LIMIT_REACHED);
            }
            if (agentLimitProfile.getMonthlyTransactionAmount().compareTo(packageCurrencyLimit.getMonthlyTransactionAmount()) >= 0) {
                throw new TransactionLimitException(ApiError.MONTHLY_TRANSACTION_AMOUNT_LIMIT_REACHED);
            } else if (agentLimitProfile.getMonthlyTransactionAmount().add(currentTransactionAmount)
                    .compareTo(packageCurrencyLimit.getMonthlyTransactionAmount()) >= 0) {
                throw new TransactionLimitException(ApiError.MONTHLY_TRANSACTION_AMOUNT_ABOVE_LIMIT,
                        packageCurrencyLimit.getMonthlyTransactionAmount().subtract(agentLimitProfile.getMonthlyTransactionAmount()).toString());
            }
        } else if (todayDate.isAfter(agentLimitProfile.getDailyCycleDate())) {
            // Month completed yesterday. need to reset date and limits
            agentLimitProfile.setMonthlyCycleDate(todayDate.plusDays(29));
            agentLimitProfile.setMonthlyTransactionCount(0);
            agentLimitProfile.setMonthlyTransactionAmount(new BigDecimal("0"));
        }
    }


    private void checkDailySendingLimit(LocalDate todayDate, BigDecimal currentTransactionAmount, PackageCurrencyLimit packageCurrencyLimit, AgentLimitProfile agentLimitProfile) {
        log.info("checking daily limits availability for date: {}, for amount: {}, with packageCurrencyLimit: {} and agentLimitProfile: {}", todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        if (todayDate.isEqual(agentLimitProfile.getDailyCycleDate())) {
            if (agentLimitProfile.getDailySendingLimit().compareTo(packageCurrencyLimit.getDailySendingLimit()) >= 0) {
                throw new TransactionLimitException(ApiError.DAILY_TRANSACTION_AMOUNT_LIMIT_REACHED);
            } else if (agentLimitProfile.getDailySendingLimit().add(currentTransactionAmount).compareTo(packageCurrencyLimit.getDailySendingLimit()) >= 0) {
                throw new TransactionLimitException(ApiError.DAILY_TRANSACTION_AMOUNT_ABOVE_LIMIT, packageCurrencyLimit.getDailySendingLimit().subtract(agentLimitProfile.getDailySendingLimit()).toString());
            }
        } else if (todayDate.isAfter(agentLimitProfile.getDailyCycleDate())) {
            agentLimitProfile.setDailySendingLimit(new BigDecimal("0"));
        } else {
            throw new TransactionLimitException(ApiError.PACKAGE_CURRENCY_NOT_AVAILABLE);
        }
    }

    private void checkWeeklySendingLimit(LocalDate todayDate, BigDecimal currentTransactionAmount, PackageCurrencyLimit packageCurrencyLimit, AgentLimitProfile agentLimitProfile) {
        log.info("checking weekly limits availability for date: {}, for amount: {}, with packageCurrencyLimit: {} and agentLimitProfile: {}", todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        if (todayDate.isEqual(agentLimitProfile.getWeeklyCycleDate()) || todayDate.isBefore(agentLimitProfile.getWeeklyCycleDate())) {
            if (agentLimitProfile.getWeeklySendingLimit().compareTo(packageCurrencyLimit.getWeeklySendingLimit()) >= 0) {
                throw new TransactionLimitException(ApiError.WEEKLY_TRANSACTION_AMOUNT_LIMIT_REACHED);
            } else if (agentLimitProfile.getWeeklySendingLimit().add(currentTransactionAmount)
                    .compareTo(packageCurrencyLimit.getWeeklySendingLimit()) >= 0) {
                throw new TransactionLimitException(ApiError.WEEKLY_TRANSACTION_AMOUNT_ABOVE_LIMIT,
                        packageCurrencyLimit.getWeeklySendingLimit().subtract(agentLimitProfile.getWeeklySendingLimit()).toString());
            }
        } else if (todayDate.isAfter(agentLimitProfile.getDailyCycleDate())) {
            // Week completed yesterday. need to reset date from current date plus 6 days and limits
            agentLimitProfile.setWeeklySendingLimit(new BigDecimal("0"));
        }
    }

    private void checkMonthlySendingLimit(LocalDate todayDate, BigDecimal currentTransactionAmount, PackageCurrencyLimit packageCurrencyLimit, AgentLimitProfile agentLimitProfile) {
        log.info("checking monthly limits availability for date: {}, for amount: {}, with packageCurrencyLimit: {} and agentLimitProfile: {}", todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        if (todayDate.isEqual(agentLimitProfile.getMonthlyCycleDate()) || todayDate.isBefore(agentLimitProfile.getMonthlyCycleDate())) {
            if (agentLimitProfile.getMonthlySendingLimit().compareTo(packageCurrencyLimit.getMonthlySendingLimit()) >= 0) {
                throw new TransactionLimitException(ApiError.MONTHLY_TRANSACTION_AMOUNT_LIMIT_REACHED);
            } else if (agentLimitProfile.getMonthlySendingLimit().add(currentTransactionAmount)
                    .compareTo(packageCurrencyLimit.getMonthlySendingLimit()) >= 0) {
                throw new TransactionLimitException(ApiError.MONTHLY_TRANSACTION_AMOUNT_ABOVE_LIMIT,
                        packageCurrencyLimit.getMonthlySendingLimit().subtract(agentLimitProfile.getMonthlySendingLimit()).toString());
            }
        } else if (todayDate.isAfter(agentLimitProfile.getDailyCycleDate())) {
            // Month completed yesterday. need to reset date and limits
            agentLimitProfile.setMonthlyCycleDate(todayDate.plusDays(29));
            agentLimitProfile.setMonthlySendingLimit(new BigDecimal("0"));
        }
    }

    public void updateTransactionLimit(AgentLimitProfile agentLimitProfile, BigDecimal currentTransactionAmount) throws TransactionLimitException {
        log.info("Update agentLimitProfile with amount: {} and plus a transaction count of all limits", currentTransactionAmount);
        agentLimitProfile.setDailyTransactionCount(agentLimitProfile.getDailyTransactionCount() + 1);
        agentLimitProfile.setWeeklyTransactionCount(agentLimitProfile.getWeeklyTransactionCount() + 1);
        agentLimitProfile.setMonthlyTransactionCount(agentLimitProfile.getMonthlyTransactionCount() + 1);

        agentLimitProfile.setDailyTransactionAmount(agentLimitProfile.getDailyTransactionAmount().add(currentTransactionAmount));
        agentLimitProfile.setWeeklyTransactionAmount(agentLimitProfile.getWeeklyTransactionAmount().add(currentTransactionAmount));
        agentLimitProfile.setMonthlyTransactionAmount(agentLimitProfile.getMonthlyTransactionAmount().add(currentTransactionAmount));

        agentLimitProfile.setDailySendingLimit(agentLimitProfile.getDailySendingLimit().add(currentTransactionAmount));
        agentLimitProfile.setWeeklySendingLimit(agentLimitProfile.getWeeklySendingLimit().add(currentTransactionAmount));
        agentLimitProfile.setMonthlySendingLimit(agentLimitProfile.getMonthlySendingLimit().add(currentTransactionAmount));
    }

    public boolean checkOperationPermission(List<PackageOperationPermission> packageOperationPermissions, Operation operation) throws OperationPermissionException {
        PackageOperationPermission packageOperationPermission = packageOperationPermissions.stream().filter(pop ->
                pop.getOperation().getOperationId().equals(operation.getOperationId()) && pop.getActive()).findAny().orElse(null);
        if (packageOperationPermission == null) {
            throw new OperationPermissionException(ApiError.OPERATION_NOT_ALLOWED);
        }
        return Boolean.TRUE;
    }

    public BigDecimal calculateCommission(BigDecimal amount, Commission commission) {
        BigDecimal commissionAmount;
        switch (commission.getCommissionType()) {
            case FIXED:
                commissionAmount = BigDecimal.valueOf(commission.getCommissionAmount());
                break;
            case PERCENTAGE:
                commissionAmount = amount.multiply(BigDecimal.valueOf(commission.getCommissionPercentage() / 100));
                break;
            case SLAB:
                commissionAmount = new BigDecimal(0);
                break;
            case FIXED_AND_PERCENTAGE:
                commissionAmount = BigDecimal.valueOf(commission.getCommissionAmount()).add(amount.multiply(BigDecimal.valueOf(commission.getCommissionPercentage() / 100)));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + commission.getCommissionType());
        }
        return commissionAmount.setScale(agentManagementPropertyHolder.getCommissionPercentageRoundingScale(), RoundingMode.HALF_UP);
    }

    public TransferCommissionDto getTransferCommission1(String uuid, AgentDetails agentDetails, Operation operation, Commission commission, BigDecimal fee) {
        return getTransferCommission(uuid, agentDetails, operation, commission, fee, agentDetailsService, packageOperationPermissionService);
    }

    public TransferCommissionDto getTransferCommission(String uuid, AgentDetails agentDetails, Operation operation,
                                                       Commission commission, BigDecimal fee, AgentDetailsService agentDetailsService,
                                                       PackageOperationPermissionService packageOperationPermissionService) {
        log.info("{} - calculating transaction commission for agent: {}, for operation: {}, with commission type: {}, for fee: {}",
                uuid, agentDetails.getAgentType(), operation.getName(), commission.getCommissionType(), fee);
        TransferCommissionDto transferCommissionDto = new TransferCommissionDto();
        BigDecimal basicCommission, agentBankerCommission, agentCommission, agentMemberCommission;
        AgentDetails agent, agentBanker;
        switch (agentDetails.getAgentType()) {
            case SUPER_AGENT:
                break;
            case AGENT_BANKER:
                basicCommission = calculateCommission(fee, commission);
                log.info("{} - agent banker commission calculated", uuid);
                transferCommissionDto.setAgentBankerCommission(basicCommission);
                transferCommissionDto.setAgentBankerAccountNumber(agentDetails.getLinkedAccountNumber());
                break;
            case AGENT_MEMBER:
                basicCommission = calculateCommission(fee, commission);

                agent = agentDetailsService.get(agentDetails.getSuperAgentId());

                agent = agentDetailsService.get(agentDetailsRepository.findByIamId(agentDetails.getSuperAgentId()).get().getAgentId().toString());
                PackageOperationPermission packageOperationPermissionForAgent =
                        packageOperationPermissionService.checkPermissionsForOperationInPackage(operation, agent.getAgentPackage());
                agentCommission = calculateCommission(basicCommission, packageOperationPermissionForAgent.getCommission());
                transferCommissionDto.setAgentCommission(agentCommission);
                transferCommissionDto.setAgentAccountNumber(agent.getLinkedAccountNumber());
                log.info("{} - agent commission calculated", uuid);


                agentBanker = agentDetailsService.get(agentDetailsRepository.findByIamId(agent.getSuperAgentId()).get().getAgentId().toString());
                PackageOperationPermission packageOperationPermissionForAgentBanker =
                        packageOperationPermissionService.checkPermissionsForOperationInPackage(operation, agentBanker.getAgentPackage());
                agentBankerCommission = calculateCommission(basicCommission, packageOperationPermissionForAgentBanker.getCommission());
                transferCommissionDto.setAgentBankerCommission(agentBankerCommission);
                transferCommissionDto.setAgentBankerAccountNumber(agentBanker.getLinkedAccountNumber());
                log.info("{} - agent banker commission calculated", uuid);

                agentMemberCommission = basicCommission.subtract(agentCommission.add(agentBankerCommission));
                transferCommissionDto.setAgentMemberCommission(agentMemberCommission);
                transferCommissionDto.setAgentMemberAccountNumber(agentDetails.getLinkedAccountNumber());
                log.info("{} - agent member commission calculated", uuid);
                break;
            case AGENT:
                basicCommission = calculateCommission(fee, commission);

                agentBanker = agentDetailsService.get(agentDetailsRepository.findByIamId(agentDetails.getSuperAgentId()).get().getAgentId().toString());
                agentBankerCommission = calculateCommission(basicCommission,
                        packageOperationPermissionService.checkPermissionsForOperationInPackage(operation, agentBanker.getAgentPackage()).getCommission());
                transferCommissionDto.setAgentBankerCommission(agentBankerCommission);
                transferCommissionDto.setAgentBankerAccountNumber(agentBanker.getLinkedAccountNumber());
                log.info("{} - agent banker commission calculated", uuid);

                agentCommission = basicCommission.subtract(agentBankerCommission);
                transferCommissionDto.setAgentCommission(agentCommission);
                transferCommissionDto.setAgentAccountNumber(agentDetails.getLinkedAccountNumber());
                log.info("{} - agent commission calculated", uuid);
                break;
        }
        return transferCommissionDto;
    }

    public String createFileAtPath(String pathToParentDirectory, MultipartFile multipartFile) throws FileProcessingException {
        if (pathToParentDirectory == null) {
            throw new FileSystemNotFoundException("Parent Directory path cannot be null");
        }
        Path directoryPath = createDirectoryAtPath(pathToParentDirectory, null);
        return createAndWriteFileAtPath(directoryPath, multipartFile);
    }

    public String createFileAtPath(String pathToParentDirectory, String pathPrefix, MultipartFile multipartFile) throws FileProcessingException {
        if (pathToParentDirectory == null) {
            throw new FileSystemNotFoundException("Parent Directory path cannot be null");
        }
        Path directoryPath = createDirectoryAtPath(pathToParentDirectory, pathPrefix);
        return createAndWriteFileAtPath(directoryPath, multipartFile);
    }

    private Path createDirectoryAtPath(String pathToParentDirectory, String pathPrefix) {
        pathToParentDirectory = pathToParentDirectory.replace("/", File.separator);
        Path directoryPath;
        try {
            if (pathPrefix == null) {
                directoryPath = Paths.get(pathToParentDirectory);
            } else {
                directoryPath = Paths.get(pathToParentDirectory + File.separator + pathPrefix);
            }
            if (Files.notExists(directoryPath)) {
                Files.createDirectory(directoryPath);
            }
        } catch (IOException ex) {
            log.error(ApiError.FILE_PROCESSING_ERROR + ": {}", ex.getMessage(), ex);
            throw new FileProcessingException(ApiError.FILE_PROCESSING_ERROR);
        }
        return directoryPath;
    }

    public String createAndWriteFileAtPath(Path pathToParentDirectory, MultipartFile multipartFile) {
        Path filePath;
        try {
            String[] filenameArray = Objects.requireNonNull(multipartFile.getOriginalFilename()).split("\\.");
            String fileName = UUID.randomUUID() + "." + filenameArray[filenameArray.length - 1];
            filePath = pathToParentDirectory.resolve(fileName);
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
            byte[] bytes = multipartFile.getBytes();
            Files.write(filePath, bytes);
            return fileName;
        } catch (IOException ex) {
            log.error(ApiError.FILE_PROCESSING_ERROR + ": {}", ex.getMessage(), ex);
            throw new FileProcessingException(ApiError.FILE_PROCESSING_ERROR);
        }
    }

    public void checkAgentValidity(AgentDetails agentDetails) throws AccountNotActiveException, BadRequestException {
        checkAgentActive(agentDetails);
//        if (agentDetails.getAgentType() != AgentType.AGENT_BANKER) {
//            log.info("Only Agent_Banker allowed to perform Account based Transactions");
//            throw new BadRequestException(ApiError.PERMISSION_DENIED);
//        }
    }

    public void checkAgentActive(AgentDetails agentDetails) throws AccountNotActiveException {
        if (agentDetails.getStatus() != AgentStatus.ACTIVE) {
            throw new AccountNotActiveException(ApiError.AGENT_NOT_ACTIVE);
        }
    }

    public void checkAgentRightsForTransaction(AgentDetails agentDetails, AgentType agentType) {
        if (agentDetails.getAgentType() != agentType) {
            log.info("Only Agent: {} is allowed to perform this Transaction", agentType.name());
            throw new BadRequestException(ApiError.PERMISSION_DENIED);
        }
    }

    public static String saveFile(String uploadFolder, MultipartFile multipartFile) {
        try {
            String fileName = UUID.randomUUID().toString();
            byte[] bytes = multipartFile.getBytes();
            Path path = Paths.get(uploadFolder, fileName);
            Files.write(path, bytes);
            return fileName;
        } catch (Exception e) {
            throw new DigibankRuntimeException(e);
        }
    }

    public TransferCommissionDto checkAgentLimitsForTransaction(String uuid, String assetName, String operationName, String currencyName,
                                                                AgentDetails agentDetails, BigDecimal amount, BigDecimal fee, LocalDateTime todayDateTime) {
        log.info("{} - Finding permissions of Asset: {}, for Operation: {}, with Currency: {}, having package: {}, with Amount: {}, with Fee: {}",
                uuid, assetName, operationName, currencyName, agentDetails.getAgentPackage().getName(), amount, fee);
        Asset asset = assetService.findByName(assetName);
        log.info("{} - Asset found", uuid);
        Operation operation = operationService.findByNameAndAsset(operationName, asset);
        log.info("{} - Operation found", uuid);
        Currency currency = currencyService.findByName(currencyName);
        log.info("{} - Currency found", uuid);
        // check that if agent has rights for this operation
        PackageOperationPermission packageOperationPermission = packageOperationPermissionService.checkPermissionsForOperationInPackage(operation, agentDetails.getAgentPackage());
        log.info("{} - operation permissions/rights are found for package", uuid);
        if (!packageOperationPermission.getActive()) {
            throw new OperationPermissionException(ApiError.PACKAGE_OPERATION_NOT_ACTIVE);
        }
        // check that if agent registered for this currency
        PackageCurrencyLimit packageCurrencyLimit = packageCurrencyLimitService.checkLimitsRegisteredForCurrencyInPackage(currency, agentDetails.getAgentPackage());
        log.info("{} - permission for currency in agent package clear", uuid);
        // check that if agent currency profile
        AgentLimitProfile agentLimitProfile = agentLimitProfileService.getAgentLimitProfileByAgentIdAndCurrencyName(agentDetails.getAgentId(), currency.getName());
        log.info("{} - Transaction profile found", uuid);
        // check that if agent has transaction limits available
        checkTransactionLimit(todayDateTime.toLocalDate(), amount, packageCurrencyLimit, agentLimitProfile);
        log.info("{} - transaction limits are available", uuid);

        updateTransactionLimit(agentLimitProfile, amount);
        log.info("{} - updated transaction limits counts and amount", uuid);

        return getTransferCommission1(uuid, agentDetails, operation, packageOperationPermission.getCommission(), fee);
    }

    public BigDecimal getLiquidityBalance(String uuid, AgentDetails agentDetails, BigDecimal agentBalance, String currencyName) {
        Currency currency = currencyService.findByCode(currencyName);
        log.info("{} - Currency found", uuid);
        // check that if agent registered for this currency
        PackageCurrencyLimit packageCurrencyLimit = packageCurrencyLimitService.checkLimitsRegisteredForCurrencyInPackage(currency, agentDetails.getAgentPackage());
        log.info("{} - permission for currency in agent package clear", uuid);
        // check that if agent currency profile
        AgentLimitProfile agentLimitProfile = agentLimitProfileService.getAgentLimitProfileByAgentIdAndCurrencyName(agentDetails.getAgentId(), currency.getName());
        BigDecimal remainingBalance = packageCurrencyLimit.getDailyTransactionAmount().min(packageCurrencyLimit.getDailyTransactionAmount());
        if (remainingBalance.longValue() > agentBalance.longValue()) {
            return agentBalance;
        } else {
            return remainingBalance;
        }
    }

    public List<LimitProfileDto> getAvailableLimits(String uuid, AgentDetails agentDetails) {


        log.info("{} - permission for currency in agent package clear", uuid);
        // check that if agent currency profile
        List<LimitProfileDto> limitProfileDtos = new ArrayList<LimitProfileDto>();
        List<PackageCurrencyLimitDto> packageCurrencyLimitList = packageCurrencyLimitService.getAllByAgentPackage(agentDetails.getAgentPackage());
        packageCurrencyLimitList.forEach(packageCurrencyLimitDto -> {
            LimitProfileDto limitProfileDto = new LimitProfileDto();

            AgentLimitProfile agentLimitProfile = agentLimitProfileService.getAgentLimitProfileByAgentIdAndCurrencyName(agentDetails.getAgentId(), packageCurrencyLimitDto.getCurrencyName());

            limitProfileDto.setTotalDailyLimit(packageCurrencyLimitDto.getDailyTransactionAmount());
            limitProfileDto.setTotalWeeklyLimit(packageCurrencyLimitDto.getWeeklyTransactionAmount());
            limitProfileDto.setTotalMonthlyLimit(packageCurrencyLimitDto.getMonthlyTransactionAmount());
//
            limitProfileDto.setTotalDailySendingLimit(packageCurrencyLimitDto.getDailySendingLimit());
            limitProfileDto.setTotalDailyReceivingLimit(packageCurrencyLimitDto.getDailyReceivingLimit());
            limitProfileDto.setTotalWeeklySendingLimit(packageCurrencyLimitDto.getWeeklySendingLimit());
            limitProfileDto.setTotalWeeklyReceivingLimit(packageCurrencyLimitDto.getWeeklyReceivingLimit());

            limitProfileDto.setAvailableDailyLimit(packageCurrencyLimitDto.getDailyTransactionAmount().subtract(agentLimitProfile.getDailyTransactionAmount()));
            limitProfileDto.setAvailableMonthlyLimit(packageCurrencyLimitDto.getMonthlyTransactionAmount().subtract(agentLimitProfile.getMonthlyTransactionAmount()));
            limitProfileDto.setAvailableWeeklyLimit(packageCurrencyLimitDto.getWeeklyTransactionAmount().subtract(agentLimitProfile.getWeeklyTransactionAmount()));

//
            limitProfileDto.setAvailableDailySendingLimit(packageCurrencyLimitDto.getDailySendingLimit().subtract(agentLimitProfile.getDailySendingLimit()));
            limitProfileDto.setAvailableDailyReceivingLimit(packageCurrencyLimitDto.getDailyReceivingLimit().subtract(agentLimitProfile.getDailyReceivingLimit()));
            limitProfileDto.setAvailableWeeklySendingLimit(packageCurrencyLimitDto.getWeeklySendingLimit().subtract(agentLimitProfile.getWeeklySendingLimit()));
            limitProfileDto.setAvailableWeeklyReceivingLimit(packageCurrencyLimitDto.getWeeklyReceivingLimit().subtract(agentLimitProfile.getWeeklyReceivingLimit()));
            limitProfileDto.setAvailableMonthlySendingLimit(packageCurrencyLimitDto.getMonthlySendingLimit().subtract(agentLimitProfile.getMonthlySendingLimit()));
            limitProfileDto.setAvailableMonthlyReceivingLimit(packageCurrencyLimitDto.getMonthlyReceivingLimit().subtract(agentLimitProfile.getMonthlyReceivingLimit()));


            limitProfileDto.setTotalDailyTransactionCount(packageCurrencyLimitDto.getDailyTransactionCount());
            limitProfileDto.setTotalWeeklyTransactionCount(packageCurrencyLimitDto.getWeeklyTransactionCount());
            limitProfileDto.setTotalMonthlyTransactionCount(packageCurrencyLimitDto.getMonthlyTransactionCount());

            limitProfileDto.setAvailableDailyTransactionCount(packageCurrencyLimitDto.getDailyTransactionCount() - agentLimitProfile.getDailyTransactionCount());
            limitProfileDto.setAvailableWeeklyTransactionCount(packageCurrencyLimitDto.getWeeklyTransactionCount() - agentLimitProfile.getWeeklyTransactionCount());
            limitProfileDto.setAvailableMonthlyTransactionCount(packageCurrencyLimitDto.getMonthlyTransactionCount() - agentLimitProfile.getMonthlyTransactionCount());

            limitProfileDto.setMonthlyCycleDate(agentLimitProfile.getMonthlyCycleDate().toString());
            limitProfileDto.setWeeklyCycleDate(agentLimitProfile.getWeeklyCycleDate().toString());

            limitProfileDto.setCurrency(packageCurrencyLimitDto.getCurrencyName());

            limitProfileDtos.add(limitProfileDto);

        });

        return limitProfileDtos;

//        BigDecimal remainingBalance = packageCurrencyLimit.getDailyTransactionAmount().min(packageCurrencyLimit.getDailyTransactionAmount());
//        if (remainingBalance.longValue() > agentBalance.longValue()) {
//            return agentBalance;
//        } else {
//            return remainingBalance;
//        }
    }


}
