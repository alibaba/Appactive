package io.appactive.rule.base.file;

import java.nio.charset.Charset;

public interface FileConstant {


    int DEFAULT_BUF_SIZE = 1024 * 1024;

    Charset DEFAULT_CHARSET = Charset.forName("utf-8");
}
