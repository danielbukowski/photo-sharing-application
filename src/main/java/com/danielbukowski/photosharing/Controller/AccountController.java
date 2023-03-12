package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.AccountDto;
import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Validated
@RestController
@RequestMapping("api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAccounts() {
        return ResponseEntity.ok(accountService.getAccounts());
    }

    @GetMapping("{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PostMapping
    public ResponseEntity<Long> createAccount(@Valid @RequestBody AccountRegisterRequest accountRegisterRequest) {
        return new ResponseEntity<>(accountService.createAccount(accountRegisterRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteAccountById(@PathVariable Long id) {
        accountService.deleteAccountById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
