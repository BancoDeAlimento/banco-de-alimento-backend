package br.recife.bancodealimentos.seguranca;

import br.recife.bancodealimentos.config.AppProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Cifra dados sensiveis (CPF, NIS) com AES-256-GCM e gera um indice cego (HMAC-SHA256)
 * para detectar duplicidade sem precisar decifrar.
 */
@Service
public class CriptografiaService {

    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;

    private final SecretKeySpec chaveCifra;
    private final SecretKeySpec chaveIndice;
    private final SecureRandom random = new SecureRandom();

    public CriptografiaService(AppProperties props) {
        String base64 = props.seguranca().chaveCriptografia();
        if (base64 == null || base64.isBlank()) {
            throw new IllegalStateException(
                    "Chave de criptografia ausente. Defina app.seguranca.chave-criptografia "
                            + "(ou APP_SEGURANCA_CHAVECRIPTOGRAFIA) com 32 bytes em Base64: openssl rand -base64 32");
        }
        byte[] mestra;
        try {
            mestra = Base64.getDecoder().decode(base64.strip());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("A chave de criptografia não é um Base64 válido.", e);
        }
        if (mestra.length < 32) {
            throw new IllegalStateException("A chave de criptografia precisa ter pelo menos 32 bytes.");
        }
        this.chaveCifra = new SecretKeySpec(derivar(mestra, "cifra-v1"), "AES");
        this.chaveIndice = new SecretKeySpec(derivar(mestra, "indice-v1"), "HmacSHA256");
    }

    /** Retorna Base64(iv || texto cifrado || tag). */
    public String cifrar(String textoPuro) {
        try {
            byte[] iv = new byte[IV_BYTES];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, chaveCifra, new GCMParameterSpec(TAG_BITS, iv));
            byte[] cifrado = cipher.doFinal(textoPuro.getBytes(StandardCharsets.UTF_8));
            byte[] saida = new byte[iv.length + cifrado.length];
            System.arraycopy(iv, 0, saida, 0, iv.length);
            System.arraycopy(cifrado, 0, saida, iv.length, cifrado.length);
            return Base64.getEncoder().encodeToString(saida);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao cifrar dado sensível.", e);
        }
    }

    public String decifrar(String base64) {
        try {
            byte[] entrada = Base64.getDecoder().decode(base64);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, chaveCifra, new GCMParameterSpec(TAG_BITS, entrada, 0, IV_BYTES));
            byte[] puro = cipher.doFinal(entrada, IV_BYTES, entrada.length - IV_BYTES);
            return new String(puro, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalStateException("Falha ao decifrar dado sensível.", e);
        }
    }

    /** HMAC-SHA256 em hexadecimal. Determinístico: mesmo CPF gera o mesmo índice. */
    public String indiceCego(String texto) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(chaveIndice);
            return HexFormat.of().formatHex(mac.doFinal(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao gerar índice.", e);
        }
    }

    private static byte[] derivar(byte[] mestra, String rotulo) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(mestra, "HmacSHA256"));
            return mac.doFinal(rotulo.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao derivar chave.", e);
        }
    }
}
