package io.appactive.java.api.rule.machine.bo;

import java.util.Objects;

import io.appactive.java.api.base.constants.AppactiveConstant;

public class MachineUnitBO {

    /**
     * machine unit flag
     */
    private String unitFlag;

    /**
     *
     */
    private Boolean checkIsCenterFlag;

    public MachineUnitBO() {
        unitFlag = AppactiveConstant.CENTER_FLAG;
        checkIsCenterFlag = true;
    }

    public MachineUnitBO(String unitFlag) {
        if (unitFlag == null){
            unitFlag = AppactiveConstant.CENTER_FLAG;
        }
        this.unitFlag = unitFlag;
        this.checkIsCenterFlag = AppactiveConstant.CENTER_FLAG.equalsIgnoreCase(unitFlag);
    }

    public String getUnitFlag() {
        return unitFlag;
    }

    public MachineUnitBO setUnitFlag(String unitFlag) {
        if (unitFlag != null) {
            this.unitFlag = unitFlag;
            this.checkIsCenterFlag = AppactiveConstant.CENTER_FLAG.equalsIgnoreCase(unitFlag);
        }
        return this;
    }

    public Boolean getCheckIsCenterFlag() {
        return checkIsCenterFlag;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        MachineUnitBO that = (MachineUnitBO)o;
        return Objects.equals(unitFlag, that.unitFlag) && Objects.equals(checkIsCenterFlag,
            that.checkIsCenterFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitFlag, checkIsCenterFlag);
    }

    @Override
    public String toString() {
        return "MachineUnitBO{" +
            "unitFlag='" + unitFlag + '\'' +
            ", checkIsCenterFlag=" + checkIsCenterFlag +
            '}';
    }
}
