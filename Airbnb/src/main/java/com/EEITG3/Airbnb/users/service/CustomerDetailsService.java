package com.EEITG3.Airbnb.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.entity.CustomerDetails;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;

@Service
public class CustomerDetailsService implements UserDetailsService {

    private final CustomerRepository repo;

    @Autowired
    public CustomerDetailsService(CustomerRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = repo.findCustomerByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者"));

        // 🔽 停權檢查
        if (Boolean.FALSE.equals(customer.isActive())) {
            String reason = customer.getSuspensionReason();
            if (reason == null || reason.isBlank()) {
                reason = "帳號已停權"; // 沒填原因就給預設字串
            }
            throw new org.springframework.security.authentication.LockedException(reason);
        }

        return new CustomerDetails(customer);
    }
}
