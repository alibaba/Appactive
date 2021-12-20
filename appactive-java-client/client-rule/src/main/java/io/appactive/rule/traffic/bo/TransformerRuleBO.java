package io.appactive.rule.traffic.bo;

import java.util.Objects;

import io.appactive.java.api.rule.traffic.bo.TransformerRule;


public class TransformerRuleBO extends TransformerRule {

    /**
     * id: primary id. unit-mapping-rule use id
     */
    private String id;

    /**
     * mod value: % mod
     * if mod == null, whole value
     */
    private Long mod;

    public String getId() {
        return id;
    }

    public TransformerRuleBO setId(String id) {
        if (id != null) { this.id = id;}
        return this;
    }

    public Long getMod() {
        return mod;
    }

    public TransformerRuleBO setMod(Long mod) {
        if (mod != null) { this.mod = mod;}
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        TransformerRuleBO that = (TransformerRuleBO)o;
        return Objects.equals(id, that.id) && Objects.equals(mod, that.mod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mod);
    }

    @Override
    public String toString() {
        return "TransformerRuleBO{" +
            "id='" + id + '\'' +
            ", mod=" + mod +
            '}';
    }
}
