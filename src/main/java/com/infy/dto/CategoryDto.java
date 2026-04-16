package com.infy.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryDto {
    private Integer id;

    @NotBlank(message = "Please provide a valid name")
    private String name;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
