package com.portfolio.dbmetadatagenerator.connection;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.iv.RandomIvGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Converter
@Component
public class PasswordConverter implements AttributeConverter<String, String> {

    private static PooledPBEStringEncryptor encryptor;

    @Value("${jasypt.encryptor.password}")
    public void setEncryptorPassword(String password) {
        PooledPBEStringEncryptor enc = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations(1000);
        config.setPoolSize(1);
        config.setIvGenerator(new RandomIvGenerator());
        enc.setConfig(config);
        PasswordConverter.encryptor = enc;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : encryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData == null ? null : encryptor.decrypt(dbData);
    }



}
