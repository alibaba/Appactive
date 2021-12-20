package io.appactive.rule.machine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import io.appactive.channel.file.FileReadDataSource;
import io.appactive.java.api.base.exception.ExceptionFactory;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.channel.listener.DataListener;
import io.appactive.java.api.rule.machine.AbstractMachineUnitRuleService;
import io.appactive.java.api.rule.machine.bo.MachineUnitBO;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rule.utils.FilePathUtil;
import io.appactive.rule.base.file.FileConstant;

public class FileMachineUnitRuleServiceImpl extends AbstractMachineUnitRuleService {

    private MachineUnitBO machineUnitBO;

    private final DataListener<MachineUnitBO> listener = new DataListener<MachineUnitBO>() {
        @Override
        public String getListenerName() {
            return "MachineUnitBO-Listener-"+this.hashCode();
        }

        @Override
        public void dataChanged(MachineUnitBO oldData, MachineUnitBO newData) {
            machineUnitBO = newData;
        }
    };

    public FileMachineUnitRuleServiceImpl() {
        initFromFile(FilePathUtil.getMachineRulePath());
    }

    public FileMachineUnitRuleServiceImpl(String filePath) {
        initFromFile(filePath);
    }

    private void initFromFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw ExceptionFactory.makeFault("filePath is empty");
        }
        ConverterInterface<String, MachineUnitBO> converterInterface = source -> JSON.parseObject(source,
            new TypeReference<MachineUnitBO>() {});
        FileReadDataSource<MachineUnitBO> fileReadDataSource = new FileReadDataSource<>(filePath,
            FileConstant.DEFAULT_CHARSET, FileConstant.DEFAULT_BUF_SIZE, converterInterface);

        fileReadDataSource.addDataChangedListener(listener);
    }


    @Override
    protected MachineUnitBO getMachineUnitBO() {
        return machineUnitBO;
    }
}
