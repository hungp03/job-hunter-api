package com.example.jobhunter.config.interceptor;

public record WhitelistRule(String pathPattern, String httpMethod) {}
