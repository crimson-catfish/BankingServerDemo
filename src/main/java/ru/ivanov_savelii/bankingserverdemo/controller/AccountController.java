package ru.ivanov_savelii.bankingserverdemo.controller;


import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.bind.annotation.*;
import ru.ivanov_savelii.bankingserverdemo.dto.request.SignUpRequest;
import ru.ivanov_savelii.bankingserverdemo.dto.response.JwtAuthenticationResponse;
import ru.ivanov_savelii.bankingserverdemo.entity.User;
import ru.ivanov_savelii.bankingserverdemo.repository.UserRepository;

import java.math.BigDecimal;
import java.time.Instant;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class AccountController {

    @Autowired
    UserRepository repository;

    @Value("${startMoney}")
    private BigDecimal startMoney;

    @Value("${secretKey}")
    private String secretKey;

    @Value("${expirationTimeHours}")
    private Integer expirationTimeHours;

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@RequestBody SignUpRequest request) {
        if (request.getLogin() == null || request.getPassword() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        System.out.println(secretKey);
        User user = new User(request.getLogin(), new BCryptPasswordEncoder().encode(request.getPassword()), startMoney);

        try {
            if (repository.findByLogin(request.getLogin()) != null)
                return new ResponseEntity<>("User with such login already exists.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error searching login in database: " + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            repository.save(user);
        } catch (Exception e) {
            return new ResponseEntity<>("Error saving user in database: " + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String token = createToken(user);

        return new ResponseEntity<>(new JwtAuthenticationResponse(token, user), HttpStatus.OK);
    }

    private String createToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .expiresAt(now.plusSeconds(expirationTimeHours * 60 * 60))
                .build();

        var encoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(secretKey.getBytes()));
        var params = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims);

        return encoder.encode(params).getTokenValue();
    }

}
