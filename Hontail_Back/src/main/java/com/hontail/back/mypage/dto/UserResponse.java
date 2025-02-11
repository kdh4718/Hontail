//package com.hontail.back.mypage.dto;
//
//import com.hontail.back.db.entity.User;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//
//@Data
//@AllArgsConstructor
//public class UserResponse {
//    private Integer id;
//    private String email;
//    private String nickname;
//    private String profileImage;
//
//    public static UserResponse fromEntity(User user) {
//        return new UserResponse(
//                user.getId(),
//                user.getUserEmail(),
//                user.getUserNickname(),
//                user.getUserImageUrl()
//        );
//    }
//}

package com.hontail.back.mypage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hontail.back.db.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    @JsonProperty("user_id")
    private Integer id;

    @JsonProperty("user_email")
    private String email;

    @JsonProperty("user_nickname")
    private String nickname;

    @JsonProperty("user_image_url")  // profile_image -> user_image_url로 변경
    private String profileImage;

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getUserEmail(),
                user.getUserNickname(),
                user.getUserImageUrl()
        );
    }
}