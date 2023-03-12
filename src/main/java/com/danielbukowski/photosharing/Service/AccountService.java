package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.AccountDto;
import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public List<AccountDto> getAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::fromAccountToAccountDto)
                .toList();
    }

    private AccountDto fromAccountToAccountDto(Account account) {
        return AccountDto.builder()
                .login(account.getEmail())
                .password(account.getPassword())
                .build();
    }

    public AccountDto getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(this::fromAccountToAccountDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "A user with this id doesn't exist")
                );
    }

    public Long createAccount(AccountRegisterRequest accountRegisterRequest) {

        if (accountRepository.findByEmail(accountRegisterRequest.getEmail()).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Account with this email already exists");

        var accountToSave = new Account();
        accountToSave.setEmail(accountRegisterRequest.getEmail());
        accountToSave.setPassword(accountRegisterRequest.getPassword());

        return accountRepository.save(accountToSave).getId();
    }

    public void deleteAccountById(Long id) {
        accountRepository.deleteById(id);
    }
}
