package io.appactive.demo.common.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultHolder <T>  implements Serializable {

    private T result;

    List<Node> chain = new ArrayList<>();

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public List<Node> getChain() {
        return chain;
    }

    public void setChain(List<Node> chain) {
        this.chain = chain;
    }

    public void addChain(String app, String unitFlag){
        chain.add(new Node(app, unitFlag));
    }

    static class Node implements Serializable{
        private String app;
        private String unitFlag;

        public Node(String app, String unitFlag) {
            this.app = app;
            this.unitFlag = unitFlag;
        }

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public String getUnitFlag() {
            return unitFlag;
        }

        public void setUnitFlag(String unitFlag) {
            this.unitFlag = unitFlag;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "app='" + app + '\'' +
                    ", unitFlag='" + unitFlag + '\'' +
                    '}';
        }
    }

    public ResultHolder(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResultHolder{" +
                "result=" + result +
                ", chain=" + chain +
                '}';
    }
}
