package com.devhow.identity.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import static com.devhow.identity.user.TimeUtilKt.now;

@Entity(name = "user_validation")
@Table(name = "user_validation")
public class UserValidation implements Serializable {
    private String token;
    private Timestamp tokenIssue;

    @Column(name = "pass_reset_token")
    private String passwordResetToken;

    @Column(name = "pass_reset_issue")
    private Timestamp passwordResetIssue;

    @Column(name = "creation")
    @CreationTimestamp
    private Timestamp creation;

    @Version
    @Column(name = "entity_version", nullable = false)
    private Long version;
    @Id
    @Column(name = "user_id", nullable = false)
    private Long user;

    public UserValidation(User user) {
        this.user = user.id;
    }

    public UserValidation() {

    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void newToken() {
        setToken(UUID.randomUUID().toString());
        setTokenIssue(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
    }

    public Timestamp getTokenIssue() {
        return tokenIssue;
    }

    public void setTokenIssue(Timestamp tokenIssue) {
        this.tokenIssue = tokenIssue;
    }

    public boolean tokenIsCurrent() {
        return Math.abs(getTokenIssue().getTime() - now().getTime()) < 1000 * 60 * 60 * 24;
    }

    public boolean passwordValidationIsCurrent() {
        return Math.abs(getPasswordResetIssue().getTime() - now().getTime()) < 1000 * 60 * 5;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public Timestamp getPasswordResetIssue() {
        return passwordResetIssue;
    }

    public void setPasswordResetIssue(Timestamp passwordResetIssue) {
        this.passwordResetIssue = passwordResetIssue;
    }

    public void newPasswordResetToken() {
        setPasswordResetToken(UUID.randomUUID().toString());
        setPasswordResetIssue(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
    }


    public Timestamp getCreation() {
        return creation;
    }

    public void setCreation(Timestamp creation) {
        this.creation = creation;
    }
}
