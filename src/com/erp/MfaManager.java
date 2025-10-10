package com.erp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MfaManager {

    private final GoogleAuthenticator gAuth;

    public MfaManager() {
        this.gAuth = new GoogleAuthenticator();
    }

    public String criarNovoSegredo() {
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public String gerarUrlQrCode(String segredo, String idUsuario) {
        String nomeEmpresa = "OptimusERP"; 

        try {
            String idUsuarioFormatado = idUsuario.replaceAll("\\s+", "");

            String label = URLEncoder.encode(nomeEmpresa + ":" + idUsuarioFormatado, StandardCharsets.UTF_8.name());
            String issuer = URLEncoder.encode(nomeEmpresa, StandardCharsets.UTF_8.name());

            String otpUrl = String.format(
                "otpauth://totp/%s?secret=%s&issuer=%s&digits=6&period=30&algorithm=SHA1",
                label, segredo, issuer
            );

            System.out.println("URL do OTP Gerada: " + otpUrl);
            
            return otpUrl;

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validarCodigo(String segredo, int codigo) {
        return gAuth.authorize(segredo, codigo);
    }
}