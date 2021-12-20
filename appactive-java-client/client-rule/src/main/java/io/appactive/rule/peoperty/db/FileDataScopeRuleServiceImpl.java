package io.appactive.rule.peoperty.db;

import java.util.Set;

import io.appactive.channel.file.FileReadDataSource;
import io.appactive.java.api.bridge.db.constants.DataScope;
import io.appactive.java.api.channel.ConverterInterface;
import io.appactive.java.api.channel.listener.DataListener;
import io.appactive.java.api.rule.property.db.DataScopeRuleService;
import io.appactive.java.api.utils.lang.StringUtils;
import io.appactive.rule.base.file.FileConstant;
import io.appactive.rule.utils.FilePathUtil;
import io.appactive.support.lang.ConcurrentHashSet;

public class FileDataScopeRuleServiceImpl implements DataScopeRuleService {

    private final Set<String> existScopeKeySet = new ConcurrentHashSet<>();

    private final Set<String> haveInitScopeKeySet = new ConcurrentHashSet<>();

    /**
     * path example: /home/admin/appactive/resources/db
     */
    private final String FILE_RESOURCES_PATH;

    public FileDataScopeRuleServiceImpl() {
        FILE_RESOURCES_PATH = FilePathUtil.getDataScopeRuleDirectoryPath();
    }

    public FileDataScopeRuleServiceImpl(String fileHeader) {
        FILE_RESOURCES_PATH = fileHeader;
    }

    @Override
    public boolean isDataScopeExist(DataScope dataScope) {
        if (dataScope == null) {
            return false;
        }
        String scopeKey = dataScope.scopeKey();
        if (existScopeKeySet.contains(scopeKey)) {
            return true;
        }
        if (haveInitScopeKeySet.contains(scopeKey)) {
            // have init,return
            return false;
        }
        // init
        initDataListener(scopeKey);

        return existScopeKeySet.contains(scopeKey);
    }



    private void initDataListener(String scopeKey) {
        String path = FILE_RESOURCES_PATH + "/"+scopeKey;
        ConverterInterface<String, String> converterInterface = source -> source;
        FileReadDataSource<String> fileReadDataSource = new FileReadDataSource<>(path,
            FileConstant.DEFAULT_CHARSET, FileConstant.DEFAULT_BUF_SIZE, converterInterface);

        DataListener<String> listener =new DataListener<String>() {
            @Override
            public String getListenerName() {
                return "DataScope-Listener-"+this.hashCode();
            }

            @Override
            public void dataChanged(String oldData, String newData) {
                if (StringUtils.isBlank(newData)){
                    existScopeKeySet.remove(scopeKey);
                    return;
                }
                // exist
                existScopeKeySet.add(scopeKey);
            }
        };

        fileReadDataSource.addDataChangedListener(listener);
        haveInitScopeKeySet.add(scopeKey);
    }

}
