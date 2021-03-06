package com.hanghae99_team3.model.user;


import com.hanghae99_team3.model.fridge.Fridge;
import com.hanghae99_team3.model.fridge.FridgeService;
import com.hanghae99_team3.model.fridge.dto.FridgeRequestDto;
import com.hanghae99_team3.model.s3.AwsS3Service;
import com.hanghae99_team3.model.user.domain.User;
import com.hanghae99_team3.model.user.dto.UserReqDto;
import com.hanghae99_team3.model.user.repository.UserRepository;
import com.hanghae99_team3.login.jwt.JwtTokenProvider;
import com.hanghae99_team3.login.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AwsS3Service awsS3Service;
    private final FridgeService fridgeService;


    public User findUserByAuthEmail(PrincipalDetails principalDetails) {
        return userRepository.findByEmail(principalDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("유저 정보가 없습니다."));
    }

    @Transactional
    public void updateUser(UserReqDto userReqDto, PrincipalDetails principalDetails) {
        User user = this.findUserByAuthEmail(principalDetails);
        //기존 이미지 삭제
        awsS3Service.deleteFile(user.getUserImg());
        //s3에 이미지 저장
        List<String> imgLinkList = awsS3Service.uploadFile(userReqDto.getUserImg());
        String imgUrl = imgLinkList.get(0);

        userReqDto.setImgUrl(imgUrl);
        user.update(userReqDto);
    }

    @Transactional
    public void deleteUser(PrincipalDetails principalDetails) {
        User user = this.findUserByAuthEmail(principalDetails);
        //기존 이미지 삭제
        awsS3Service.deleteFile(user.getUserImg());
        userRepository.deleteById(user.getId());
    }

    @Transactional(readOnly = true)
    public List<Fridge> getFridge(PrincipalDetails principalDetails) {
        User user = this.findUserByAuthEmail(principalDetails);

        return fridgeService.getFridge(user);
    }

    @Transactional
    public void createFridge(PrincipalDetails principalDetails, List<FridgeRequestDto> fridgeRequestDtoList) {
        User user = this.findUserByAuthEmail(principalDetails);

        fridgeService.createFridge(fridgeRequestDtoList,user);
    }

    @Transactional
    public void updateFridge(PrincipalDetails principalDetails, List<FridgeRequestDto> fridgeRequestDtoList) {
        User user = this.findUserByAuthEmail(principalDetails);

        fridgeService.updateFridge(fridgeRequestDtoList,user);
    }



//    public Long join(SignupMemberDto memberDto) {
//        String username = memberDto.getUsername();
//        Optional<User> found = userRepository.findByEmail(username);
//        if (found.isPresent()) {
//            throw new IllegalArgumentException("중복된 사용자 ID 가 존재합니다.");
//        }
//
//        User user = new User(username);
//        return userRepository.save(user).getId();
//    }

//    public Map<String, String> login(LoginMemberDto memberDto) {
//        Map<String, String> token = new HashMap<>();
//
//        User user = userRepository.findByEmail(memberDto.getUsername())
//                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 username 입니다."));
//
//        token.put("Access-Token", jwtTokenProvider.createToken(user.getEmail(), user.getRole()));
//        return token;
//    }




}
