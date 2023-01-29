package com.bitstudy.app.repository;

import com.bitstudy.app.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

/* <UserAccount, Long> 을 <UserAccount, String> 으로 바꿈 */
public interface Ex16_6_UserAccountRepository extends JpaRepository<UserAccount, String> {
}
