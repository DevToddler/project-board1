package com.bitstudy.app.serucity;

import com.bitstudy.app.dto.UserAccountDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/* 시큐리티 인증과 직접적인 관계가 있음.
* domain > UserAccount.java 가 참고할만한 정보들을 가지고 있음.
* 가보면 userId, pw, email, nickname, memo 가 있는데 이중에 userId랑 pw 는 필수로 들어가야 한다.  */
public record BoardPrincipal(
        /* 여기에 쓰이는 이름은 엔티티의 이름을 따라갈 필요는 없다. 여기서 쓸 이름으로 그냥 만들면 된다.
        *  단지 인증할때 필요한 정보들을 쓰면 된다. */
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities, /* 이거 따로 넣어준거임. 이유는 요 아래 있음*/
        String email,
        String nickname,
        String memo
) implements UserDetails { /* UserDetails 을 구현할거다.*/

    /* 기본적인건 Alt + Insert 해서 나오는거에서 Implement Method 를 선택하면 다 생성된다.
     * 자동으로 생성되는것중에 우리가 별도로 건드리지 않아도 되는 코드들이 있는데
     * ID 랑 PW 부분 제외한 부분이 그것들이다. 맨 아래쪽에 is~ 로 시작하는 메서드들은 우리가 안건드릴거다. 다 true 면된다.
     * */


    /* 이거 두개 중요*/
    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return password; }


    /* 이건 권환에 관련된 부분 - 인증과 권한은 다른거임 헷갈리지 말것.
     * 인증은 로그인을 했냐 안했냐
     * 권한은 로그인한 사용자가 가지는 권한 (관리자권한, 유져 권한 같은거)
     * 우리는 별도로 관리자 유저를 나눌게 아니라서 딱히 중요도가 높진 않다. 그래도 매개변수로 받긴 해야 하니까
     * BoardPrincipal 에 매개변수로 하나 넣어주자  */
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        /* 매개변수로 하나 넣어주고, null 을 authorities 로 변경하기 */
        //return null;
        return authorities;
    }


    /* 얘네는 안중요 */
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }


    /* 생성자 만들기 - 팩토리 메서드 of() 로 만들기 */
    public static BoardPrincipal of(String username, String password, String email, String nickname, String memo) {
        /* 권한정보를 컬렉션 데이터로 저장되기때문에 중복은 없다. 그래서 자료구조는 Set 이 되야한다.*/
        Set<RoleType> roleTypes = Set.of(RoleType.USER);

        return new BoardPrincipal(
                username,
                password,

                roleTypes.stream()
                        .map(RoleType::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet())
                /** roleTypes 해설: 권환에 관련된 정보를 넣어줄거임.
                 그냥 두면 roleTypes 의 타입은 RoleType 타입이다. (위에 Set<BoardPrincipal.RoleType> 때문임)
                 그런데 맨 위에 매개변수 부분 보면 'Collection<? extends GrantedAuthority>'가 있다. 그거에 관련된 제네릭을 써야 하는데 RoleType 타입이라 에러난다.
                 그래서 타입을 변환을 하기 위해서 stream() 을 이용할거다.
                 .map(BoardPrincipal.RoleType::getName): 맵을 이용해서 RoleType 에서 name을 가져올거고
                 SimpleGrantedAuthority 로 생성자를 만들거다. (Ctrl + B 해보면 거기에 GrantedAuthority 로 구현하고 있음.)
                 collect(Collectors.toUnmodifiableSet()): 그걸 모아서(collect) toUnmodifiableSet 을 이용해서 바꾼다.

                 이렇게 하면 최초에 BoardPrincipal 이 생성될때 인증정보를 만들게 되는데 new 가 돌면서 roleType이 만들어질거다.
                 */
                ,
                email,
                nickname,
                memo
        );
    }

    /* 위에서 Set<BoardPrincipal.RoleType> 부분에서 쓰임*/
    public enum RoleType {
        USER("ROLE_USER"); /* 스프링시큐리티에서 권한 표현을 하는 문자열에는 무조건 앞에 ROLE_ 로 시작해야 한다.*/

        @Getter
        private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }

    /* UserAccountDto 로부터 BoardPrincipal 을 역으로 만들어야 하는 경우도 있을거다. */
    public static BoardPrincipal from(UserAccountDto dto) {
        return BoardPrincipal.of(
                dto.userId(),
                dto.userPassword(),
                dto.email(),
                dto.nickname(),
                dto.memo()
        );
    }
    /* Principal 을 받아서 dto 로 변환하는 작업 하기*/
    public UserAccountDto toDto() {
        return UserAccountDto.of(
                username,
                password,
                email,
                nickname,
                memo
        );
    }

    /* 다 했으면 config > Ex19_1_SecurityConfig 로 돌아가기*/
}
