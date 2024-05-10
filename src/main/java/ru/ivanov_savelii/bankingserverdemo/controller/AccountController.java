package ru.ivanov_savelii.bankingserverdemo.controller;


import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.bind.annotation.*;
import ru.ivanov_savelii.bankingserverdemo.dto.request.SignInRequest;
import ru.ivanov_savelii.bankingserverdemo.dto.request.SignUpRequest;
import ru.ivanov_savelii.bankingserverdemo.dto.response.SignResponse;
import ru.ivanov_savelii.bankingserverdemo.entity.User;
import ru.ivanov_savelii.bankingserverdemo.repository.UserRepository;

import java.math.BigDecimal;
import java.time.Instant;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class AccountController {

    @Autowired
    UserRepository repository;

    @Autowired
    AuthenticationManager authenticationManager;

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

        return new ResponseEntity<>(new SignResponse(token, user), HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<Object> signIn(@RequestBody SignInRequest request) {
        if (request.getLogin() == null || request.getPassword() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);


        try {
            System.out.println(request.getLogin());
            System.out.println(request.getPassword());
            System.out.println(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
            System.out.println(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()).getCredentials());


            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            return new ResponseEntity<>("Wrong login or password." + e, HttpStatus.BAD_REQUEST);
        }

        try {
            User user = repository.findByLogin(request.getLogin());
            String token = createToken(user);
            return new ResponseEntity<>(new SignResponse(token, user), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating token: " + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }


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
