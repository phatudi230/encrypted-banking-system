//Banking System that encrypts Account number

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;
import java.util.Base64;


//trasaction history class 
class Transaction {
    private String type;
    private double amount;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }
}

//beneficiary class 
class Beneficiary {
    private String name;
    private String accountNumber;

    public Beneficiary(String name, String accountNumber) {
        this.name = name;
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}


// account information class
class Account {
    private String encryptedAccountNumber;
    private String accNumber;
    private String crdHolder;
    private double balance;
    private List<Transaction> transactions;
    private List<Beneficiary> beneficiaries;
    

    public Account(String crdHolder, String accNumber) throws Exception {
        this.crdHolder = crdHolder;
        this.accNumber = accNumber;
        this.balance = 0.00;
        this.encryptedAccountNumber = EncryptionUtil.encrypt(accNumber);
        this.transactions = new ArrayList<>();
        this.beneficiaries = new ArrayList<>();
    }
    public  void printHistory() {
        System.out.println("Transaction History for Account: " + encryptedAccountNumber);
        for (Transaction transaction : transactions) {
            System.out.println(transaction.getType() + ": " + transaction.getAmount());
        }
    }

    public String getAccountNumber() throws Exception {
        return EncryptionUtil.decrypt(encryptedAccountNumber);
    }

    public String getOwnerName() {
        return crdHolder;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactions.add(new Transaction("Deposit", amount));
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactions.add(new Transaction("Withdraw", amount));
        }
    }
    public void addBeneficiary(String beneficiaryName, String beneficiaryAccountNumber) {
        beneficiaries.add(new Beneficiary(beneficiaryName,beneficiaryAccountNumber));
    }

    public List<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }
     
}
//AES encryption decryption algorithm class
class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "MysupersecretKey".getBytes(); //128-bits key

    public static String encrypt(String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }
}
//data storage class
class Bank {
    private List<Account> accounts = new ArrayList<>();

    public void openAccount(String accNumber, String crdHolder) {
        try {
            accounts.add(new Account(crdHolder,accNumber));
        } catch (Exception e) {
            System.out.println("Error opening account: " + e.getMessage());
        }
    }

    public void closeAccount(String accNumber) {
        try {
            accounts.removeIf(account -> {
                try {
                    return account.getAccountNumber().equals(accNumber);
                } catch (Exception e) {
                    System.out.println("Error closing account: " + e.getMessage());
                    return false;
                }
            });
        } catch (Exception e) {
            System.out.println("Error closing account: " + e.getMessage());
        }
    }

    public Account getAccount(String accNumber) {
        try {
            for (Account account : accounts) {
                if (account.getAccountNumber().equals(accNumber)) {
                    return account;
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving account: " + e.getMessage());
        }
        return null;
    }
    public void deposit(double depositAmount) {
        if (depositAmount > 0) {
            balance += depositAmount;
            
            transactions.add(new Transaction("Deposit", depositAmount));
        }
    }

    public void withdraw(double withdrawAmount) {
        if (withdrawAmount > 0 && withdrawAmount <= balance) {
            balance -= withdrawAmount;
            
            transactions.add(new Transaction("Withdraw", withdrawAmount));
        }
    }

    public void transfer(Account fromAccount, Account toAccount, double amount) {
        if (fromAccount != null && toAccount != null && amount > 0 && fromAccount.getBalance() >= amount) {
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            System.out.println("~~~~~~~~~~~Transfer successful.~~~~~~~~~~~~~");
            transactions.add(new Transaction("Transfer", transferAmount));
        } else {
            System.out.println("Transfer failed. Check account details and balance.");
        }
    }
    

    public void generateDailyReport() {
        System.out.println("\n~~~~~~~ Account Details~~~~~:");
        for (Account account : accounts) {
            try {
                System.out.println("Account Number: " + account.getAccountNumber() +
                                   "\nAccount Holder: " + account.getOwnerName() +
                                   "\nBalance: R " + account.getBalance());
            } catch (Exception e) {
                System.out.println("Error generating report for account: " + e.getMessage());
            }
        }
    }
}
//main class
public class BankingSystem {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner sc = new Scanner(System.in);

      
            try {
                System.out.println("```````````````````````````````\nHello, welcome to SwiftPay!!\nLet's create an Account.\n````````````````````````````````");
                System.out.println("Enter cardHolder:");
                String crdHolder = sc.nextLine();
                System.out.println("Enter your Account Number:");
                String accNumber = sc.nextLine();
                bank.openAccount(accNumber,crdHolder);
        while (true) {

                System.out.println("____________________________________________________________\n");
                System.out.println("Select options:\n1. Deposit\n2. Transfer\n3. Withdraw\n4. Transactions\n5. Add Beneficiary\n6. Details\n7. Exit\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                int option = sc.nextInt();
                sc.nextLine(); // Consume newline

                if (option == 7) {
                    break;
                }

                System.out.println("Enter your Account Number:");
                accNumber = sc.nextLine();
                Account account = bank.getAccount(accNumber);

                if (account != null) {
                    switch (option) {
                        case 1:
                            System.out.println("Enter amount to deposit:");
                            double depositAmount = sc.nextDouble();
                            System.out.println("~~~~~~~~Deposit successful.~~~~~~~~~~");
                            account.deposit(depositAmount);
                            break;
                        case 2:
                            System.out.println("Enter recipient's Account Number:");
                            String recipientAccNumber = sc.nextLine();
                            Account recipientAccount = bank.getAccount(recipientAccNumber);
                            System.out.println("Enter amount to transfer:");
                            double transferAmount = sc.nextDouble();
                            bank.transfer(account, recipientAccount, transferAmount);
                            break;
                        case 3:
                            System.out.println("Enter amount to withdraw:");
                            double withdrawAmount = sc.nextDouble();
                            System.out.println("~~~~~~~~~~~~Withdrawal  successful.~~~~~~~~~~~~");
                            account.withdraw(withdrawAmount);
                            break;
                        case 4:
                            account.printHistory();
                            break;
                        case 5:
                            System.out.println("Enter beneficiary name:");
                            String beneficiaryName = sc.nextLine();
                            System.out.println("Enter beneficiary account number:");
                            String beneficiaryAccountNumber = sc.nextLine();
                            account.addBeneficiary(beneficiaryName, beneficiaryAccountNumber);
                            System.out.println("Beneficiary added successfully.");
                            break;
                        case 6:
                            bank.generateDailyReport();
                            break;
                        default:
                            System.out.println("Invalid option.");
                    }
                   } else {
                    System.out.println("Account not found.");
                }
            }
        }
                catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
        }
        sc.close();
    

}
}
    

















































































//*******************************************the end**********************
