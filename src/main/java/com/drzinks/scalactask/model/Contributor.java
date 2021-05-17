package com.drzinks.scalactask.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contributor implements Comparable<Contributor>{
    @JsonProperty("login")
    private String name;
    @JsonProperty("contributions")
    private int contributions;

    @Override
    public int compareTo(Contributor o) {
        return this.contributions-o.getContributions();
    }
}