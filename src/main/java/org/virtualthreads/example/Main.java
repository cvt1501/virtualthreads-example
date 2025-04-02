package org.virtualthreads.example;

import org.virtualthreads.example.domain.Account;
import org.virtualthreads.example.domain.LinkedBankAccount;
import org.virtualthreads.example.domain.LinkedBankUser;
import org.virtualthreads.example.domain.User;
import org.virtualthreads.example.infrastructure.GenericMemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        var accountRepository = GenericMemoryRepository.getInstance(Account.class);
        var linkedBankAccountRepository = GenericMemoryRepository.getInstance(LinkedBankAccount.class);
        var userRepository = GenericMemoryRepository.getInstance(User.class);
        var linkedBankUserRepository = GenericMemoryRepository.getInstance(LinkedBankUser.class);

        accountRepository.save(args[0], new Account(args[0], 55));
        linkedBankAccountRepository.save(args[0], new LinkedBankAccount(args[0], 55));
        userRepository.save(args[0], new User(1, args[0]));
        linkedBankUserRepository.save(args[0], new LinkedBankUser(1, args[0]));

        var start = System.currentTimeMillis();

        try(var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<Optional<?>>> tasks = List.of(
                    () -> accountRepository.findById(args[0]),
                    () -> linkedBankAccountRepository.findById(args[0]),
                    () -> userRepository.findById(args[0]),
                    () -> linkedBankUserRepository.findById(args[0])
            );

            List<Future<Optional<?>>> futures = new ArrayList<>();
            tasks.forEach(task -> futures.add(executor.submit(task)));

            for (var future : futures) {
                future.get().ifPresentOrElse(
                        data -> {
                            switch (data) {
                                case Account a -> System.out.println("Account balance: " + a.balance());
                                case LinkedBankAccount lba -> System.out.println("LinkedBankAccount balance: " + lba.balance());
                                case User u -> System.out.println("User CPF: " + u.cpf());
                                case LinkedBankUser lbu -> System.out.println("LinkedBankUser CPF: " + lbu.cpf());
                                default -> System.out.println("Unknown type " + data.getClass());
                            }
                        },
                        () -> System.out.println("Data not found")
                );
            }
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            Throwable cause = e.getCause();

            System.out.println(cause.getMessage());
        }

        var end = System.currentTimeMillis();

        System.out.println("Execution time: " + (end - start) + " ms");
    }
}