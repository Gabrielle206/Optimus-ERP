package com.erp;

import org.jasypt.digest.StandardStringDigester;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import io.github.cdimascio.dotenv.Dotenv; 

public class CryptoManager {

    private static final CryptoManager instance = new CryptoManager();
    
    private final StandardPBEStringEncryptor textEncryptor;
    private final StandardStringDigester passwordDigester;

    private CryptoManager() {
        Dotenv dotenv = Dotenv.load();
        String masterPassword = dotenv.get("ERP_MASTER_KEY");

        if (masterPassword == null || masterPassword.isEmpty()) {
            System.err.println("AVISO DE SEGURANÇA: A chave mestra 'ERP_MASTER_KEY' não foi encontrada no ficheiro .env!");
            masterPassword = "fallback-key-insegura";
        }

        textEncryptor = new StandardPBEStringEncryptor();
        textEncryptor.setPassword(masterPassword); 
        textEncryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256"); 
        textEncryptor.setIvGenerator(new RandomIvGenerator());

        passwordDigester = new StandardStringDigester(); 
        passwordDigester.setAlgorithm("SHA-256");
        passwordDigester.setIterations(1000);
    }

    public static CryptoManager getInstance() {
        return instance;
    }

    public String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        return textEncryptor.encrypt(data);
    }

    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }
        try {
            return textEncryptor.decrypt(encryptedData);
        } catch (Exception e) {
            return encryptedData; 
        }
    }

    public String hashPassword(String password) {
        return passwordDigester.digest(password);
    }

    public boolean checkPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        return passwordDigester.matches(plainPassword, hashedPassword);
    }

	public String encrypt(double valor) {
		return encrypt(Double.toString(valor));
	}

	public String hashPassword(double valor) {
		return hashPassword(Double.toString(valor));
	}
}
