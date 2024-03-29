package com.henriquemgomes.cmpconverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.henriquemgomes.cmpconverter.exceptions.CmpConverterException;
import com.henriquemgomes.cmpconverter.models.DistinguishedNameModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Utils {
    public static RDN[] generateDn(DistinguishedNameModel dnModel) {
		ArrayList<RDN> rdnsList = new ArrayList<>();
		if (dnModel.getCommonName() != null) {
			RDN commonName = new RDN(new ASN1ObjectIdentifier("2.5.4.3"), new DERUTF8String(dnModel.getCommonName()));
			rdnsList.add(commonName);
		}
		if (dnModel.getCountry() != null) {
			RDN country = new RDN(new ASN1ObjectIdentifier("2.5.4.6"), new DERUTF8String(dnModel.getCountry()));
			rdnsList.add(country);
		}
		if (dnModel.getLocality() != null) {
			RDN locality = new RDN(new ASN1ObjectIdentifier("2.5.4.7"), new DERUTF8String(dnModel.getLocality()));
			rdnsList.add(locality);
		}
		if (dnModel.getStateOrProvince() != null) {
			RDN stateOrProvinceName = new RDN(new ASN1ObjectIdentifier("2.5.4.8"), new DERUTF8String(dnModel.getStateOrProvince()));
			rdnsList.add(stateOrProvinceName);
		}
		if (dnModel.getOrganization() != null) {
			RDN organization = new RDN(new ASN1ObjectIdentifier("2.5.4.10"), new DERUTF8String(dnModel.getOrganization()));
			rdnsList.add(organization);
		}

		if (dnModel.getOrganizationalUnits() != null) {
			for (String ou : dnModel.getOrganizationalUnits()) {
				if (ou != null) {
					rdnsList.add(new RDN(new ASN1ObjectIdentifier("2.5.4.11"), new DERUTF8String(ou)));
				}
			}
		}

		RDN[] rdns = new RDN[rdnsList.size()];
		rdns = rdnsList.toArray(rdns);
		return rdns;
	}

	public static DistinguishedNameModel parseDn(RDN[] distinguishedName) throws IOException {
		DistinguishedNameModel dnModel = new DistinguishedNameModel();
		List<String> organizationalUnits = new ArrayList<>();

		for (RDN rdn : distinguishedName) {
			switch (rdn.getFirst().getType().getId()) {
				case "2.5.4.3":
					dnModel.setCommonName(new String(rdn.getFirst().getValue().toString().getBytes()));
					break;
				case "2.5.4.6":
					dnModel.setCountry(new String(rdn.getFirst().getValue().toString().getBytes()));
					break;
				case "2.5.4.7":
					dnModel.setLocality(new String(rdn.getFirst().getValue().toString().getBytes()));
					break;
				case "2.5.4.8":
					dnModel.setStateOrProvince(new String(rdn.getFirst().getValue().toString().getBytes()));
					break;
				case "2.5.4.10":
					dnModel.setOrganization(new String(rdn.getFirst().getValue().toString().getBytes()));
					break;
				case "2.5.4.11":
					organizationalUnits.add(new String(rdn.getFirst().getValue().toString().getBytes()));
					break;
				default:
					break;
			}
		}

		dnModel.setOrganizationalUnits(organizationalUnits);

		return dnModel;
	}

    public static Certificate getCertificateFromBase64(String jsonCACertificate) throws Exception {
		byte[] caCert = null;
		try {
			caCert = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(jsonCACertificate);
		} catch (Exception e) {
			String msg = "Erro ao decodificar o recipient_certificate, verifique o campo";
			throw new Exception(msg);
		}
		Certificate cert = null;
		try {
			Reader certReader = new StringReader(new String(caCert));
			PEMParser pParser = new PEMParser(certReader);
			Object certificate = pParser.readObject();
			if (certificate == null) {
				throw new IOException();
			}
			X509CertificateHolder x509Cert = (X509CertificateHolder) certificate;
			cert = x509Cert.toASN1Structure();
		} catch (IOException e) {
			byte[] caCertificateData = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(jsonCACertificate);
			X509CertificateHolder certHolder = null;
			try {
				certHolder = new X509CertificateHolder(caCertificateData);
			} catch (Exception e1) {
				String msg = "EOF Prematuro no recipient_certificate, verifique o campo";
				throw new Exception(msg);
			}
			cert = certHolder.toASN1Structure();
		}
		return cert;
	}

	public static SubjectPublicKeyInfo instantiatePublicKeyFromB64(String b64PubKey) throws CmpConverterException {
		log.info("Instantiating publickey from b64");
		byte[] publicKeyData = null;
		try {
			publicKeyData = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(b64PubKey);
		} catch (Exception ex) {
			String[] logInfo = {ex.getMessage()};
			throw new CmpConverterException(
				"cmp.error.instantiate.pubkey",
				"Cannot instantiate pubkey from given b64: "+ ex.getMessage(),
				300,
				HttpStatus.UNPROCESSABLE_ENTITY,
				logInfo
			);
		}

		String publicKey = new String(publicKeyData);
		try (StringReader stringReader = new StringReader(publicKey);
				PEMParser pemParser = new PEMParser(stringReader)) {
			SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) pemParser.readObject();
			if (subjectPublicKeyInfo == null) {
				throw new NullPointerException();
			}
			log.info("The publickey was successfully instantiated.");
			return subjectPublicKeyInfo;
		} catch (NullPointerException nullPointerEx) {
			log.info("Given publickey B64 isn't PEM encoded. Proceeding to decode plain b64.");
			final ByteArrayInputStream bIn = new ByteArrayInputStream(publicKeyData);
			ASN1InputStream dIn = new ASN1InputStream(bIn);
			SubjectPublicKeyInfo subjectPublicKeyInfo = null;
			try {
				subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(dIn.readObject());
			} catch (Exception e) {
				String[] logInfo = {e.getMessage()};
				throw new CmpConverterException(
					"cmp.error.read.pubkey",
					"An error has occoured while trying to read the given b64 publickey: "+ e.getMessage(),
					301,
					HttpStatus.BAD_REQUEST,
					logInfo
				);
			}
			log.info("The publickey was successfully instantiated.");
			return subjectPublicKeyInfo;
		} catch (Exception e) {
			String[] logInfo = {e.getMessage()};
			throw new CmpConverterException(
					"cmp.error.read.pubkey",
					"The given publickey b64 is not valid",
					301,
					HttpStatus.BAD_REQUEST,
					logInfo
				);
		}
	}

	public static String translateCMPMessageType(int type) throws CmpConverterException {
		switch (type) {
			case 2:
				return "cr";
			case 3:
				return "cp";
			case 11:
				return "rr";
			case 12:
				return "rp";
			default:
				throw new CmpConverterException("translate.type.error", "Unsupported Type.", 901, HttpStatus.BAD_REQUEST, null);
		}
	}

	public static String decodeHexBytesToString(byte[] bytes) {
        String uidHex = Hex.toHexString(bytes);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < uidHex.length(); i += 2) {
            String hexByte = uidHex.substring(i, i + 2);
            int decimal = Integer.parseInt(hexByte, 16);
            char character = (char) decimal;
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }

	public static byte[] decodeHexString(String input) {
        String[] hexOctets = input.split(":");

        byte[] bytes = new byte[hexOctets.length];
        for (int i = 0; i < hexOctets.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexOctets[i], 16);
        }

        return bytes;
    }

    public static String encodeToHexString(byte[] bytes) {
        StringBuilder encodedString = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (encodedString.length() > 0) {
                encodedString.append(":");
            }
            encodedString.append(String.format("%02X", bytes[i]));
        }

        return encodedString.toString();
    }

	public static BigInteger decodeHexToBigInteger(String hexOctetString) {
        String[] hexOctets = hexOctetString.split(":");
        BigInteger decimalInteger = BigInteger.ZERO;

        for (String hexOctet : hexOctets) {
            decimalInteger = decimalInteger.shiftLeft(8).add(new BigInteger(hexOctet, 16));
        }

        return decimalInteger;
    }

	public static byte[] encodeBigInteger(BigInteger number) {
        byte[] bytes = number.toByteArray();

        // Remove leading zero byte if present
        if (bytes[0] == 0) {
            byte[] trimmedBytes = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, trimmedBytes, 0, trimmedBytes.length);
            return trimmedBytes;
        }

        return bytes;
    }

	public static PrivateKey instantiatePrivateKeyFromB64(String jsonPrivKey)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		// Provider necessário para KeyFactory funcionar
		java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		byte[] privKey = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(jsonPrivKey);
		PrivateKey clientPvtKey = null;
		// tenta construir a privateKey do tipo PEM inicialmente
		try {
			Reader privReader = new StringReader(new String(privKey));
			PEMParser pParserKey = new PEMParser(privReader);
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
			Object clientKeyPair = null;
			try {
				clientKeyPair = pParserKey.readObject();
			} catch (IOException e) {
				String msg = "Erro ao parsear o PEM da private_key, verifique o campo";
				log.error(msg + "; Motivo: " + e.getMessage());
			} catch (StringIndexOutOfBoundsException e) {
				String msg = "Erro ao parsear o PEM da private_key, tamanho da string fora do intervalo, verifique o campo";
				log.error(msg + "; Motivo: " + e.getMessage());
			} catch (Exception e) {
				String msg = "Erro ao parsear o PEM da private_key, verifique o campo";
				log.error(msg + "; Motivo: " + e.getMessage());
			}
			if (clientKeyPair instanceof PrivateKeyInfo) {
				clientPvtKey = converter.getPrivateKey((PrivateKeyInfo) clientKeyPair);
			} else {
				clientPvtKey = converter.getPrivateKey(((PEMKeyPair) clientKeyPair).getPrivateKeyInfo());
			}
		} catch (NullPointerException e) {
			// se não conseguiu construir é porque a chave está no formato base64
			log.warn("A chave privada não é do tipo PEM.");
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privKey);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			try {
				clientPvtKey = keyFactory.generatePrivate(keySpec);
			} catch (Exception ex) {
				String msg = "EOF Prematuro na private_key, verifique o campo";
				log.error(msg + "; Motivo: " + e.getMessage());
			}

		}
		return clientPvtKey;
	}

	public static PublicKey getPub(String pub) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = Base64.decodeBase64(pub);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
	}
}
