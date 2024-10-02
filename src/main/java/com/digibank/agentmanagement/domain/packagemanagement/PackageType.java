package com.digibank.agentmanagement.domain.packagemanagement;

import java.util.Arrays;
import java.util.List;

public enum PackageType {
    STANDARD, FEATURED;

    public List<PackageType> getAll(){
        return Arrays.asList(PackageType.values().clone());
    }
}
