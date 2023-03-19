package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.AccountDto;
import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Dto.ChangePasswordRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Exception.AccountNotFoundException;
import com.danielbukowski.photosharing.Mapper.AccountMapper;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;

    public List<AccountDto> getAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::fromAccountToAccountDto)
                .toList();
    }


    public AccountDto getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(accountMapper::fromAccountToAccountDto)
                .orElseThrow(() -> new AccountNotFoundException(
                        "An account with this id doesn't exist")
                );
    }

    @Transactional
    public Long createAccount(AccountRegisterRequest accountRegisterRequest) {
        if (accountRepository.findByEmailIgnoreCase(accountRegisterRequest.getEmail()).isPresent())
            throw new RuntimeException("An account with this email already exists");

        var accountToSave = new Account();
        accountToSave.setEmail(accountRegisterRequest.getEmail());
        accountToSave.setPassword(passwordEncoder.encode(accountRegisterRequest.getPassword().trim()));
        return accountRepository.save(accountToSave).getId();
    }

    @Transactional
    public void deleteAccountById(Long id) {
        if (accountRepository.findById(id).isEmpty())
            throw new AccountNotFoundException("An account with this id doesn't exist");

        accountRepository.deleteById(id);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest changePasswordRequest) {
        var account = accountRepository.getByEmailIgnoreCase(email);
        String oldHashedPassword = account.getPassword();
        String newPassword = changePasswordRequest.getNewPassword();

        if (passwordEncoder.matches(newPassword, oldHashedPassword))
            throw new RuntimeException("The old password should not be the same as the new one");

        account.setPassword(passwordEncoder.encode(newPassword));
    }
}
