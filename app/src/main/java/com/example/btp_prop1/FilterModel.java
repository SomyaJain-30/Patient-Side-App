package com.example.btp_prop1;

import java.util.List;

public class FilterModel {
    private Integer experience;
    private Integer exp_idx, gen_idx;
    private Integer gender;
    private List<String> roles;
    private List<String> langs;


    public FilterModel(Integer experience, Integer gender, List<String> roles, List<String> langs) {
        this.experience = experience;
        this.gender = gender;
        this.roles = roles;
        this.langs = langs;
        exp_idx=-1; gen_idx=-1;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getLangs() {
        return langs;
    }

    public void setLangs(List<String> langs) {
        this.langs = langs;
    }

    public Integer getExp_idx() {
        return exp_idx;
    }

    public void setExp_idx(Integer exp_idx) {
        this.exp_idx = exp_idx;
    }

    public Integer getGen_idx() {
        return gen_idx;
    }

    public void setGen_idx(Integer gen_idx) {
        this.gen_idx = gen_idx;
    }
}
