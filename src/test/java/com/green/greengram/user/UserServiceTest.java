package com.green.greengram.user;

import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.user.model.SignInPostReq;
import com.green.greengram.user.model.SignInPostRes;
import com.green.greengram.user.model.SignUpPostReq;
import com.green.greengram.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({ CustomFileUtils.class, UserServiceImpl.class })
@TestPropertySource(
     properties = {
             "file.directory=D:/2024-01/download/greengram_tdd/"
     }
)
class UserServiceTest {

    @Value("${file.directory}") String uploadPath;
    @MockBean UserMapper mapper;
    @Autowired UserService service;


    @Test
    void signUpPostReq() throws IOException {
        String p1Upw = "abc";
        SignUpPostReq p1 = new SignUpPostReq();
        p1.setUserId(100);
        p1.setUpw(p1Upw);

        given(mapper.signUpPostReq(p1)).willReturn(1);

        String p2Upw = "def";
        SignUpPostReq p2 = new SignUpPostReq();
        p2.setUserId(200);
        p2.setUpw("def");

        given(mapper.signUpPostReq(p2)).willReturn(2);

        MultipartFile fm1 = new MockMultipartFile(
                "pic", "3ef374e3-22bf-4ac6-9fa8-581f696dea4b.png", "image/png",
                new FileInputStream("D:/2024-01/download/greengram_tdd/user/3/3ef374e3-22bf-4ac6-9fa8-581f696dea4b.png")

        );

        int result1 = service.signUpPostReq(fm1, p1);
        assertEquals(1, result1);

        File savedFile1 = new File(uploadPath
                , String.format("%s/%d/%s", "user", p1.getUserId(), p1.getPic()));
        assertTrue(savedFile1.exists(), "1. 파일이 만들어지지 않음");
        savedFile1.delete();

        assertNotEquals(p1Upw, p1.getUpw());


        int result2 = service.signUpPostReq(fm1, p2);
        assertEquals(2, result2);

        File savedFile2 = new File(uploadPath
                , String.format("%s/%d/%s", "user", p2.getUserId(), p2.getPic()));
        assertTrue(savedFile2.exists(), "2. 파일이 만들어지지 않음");
        savedFile2.delete();

        assertNotEquals(p2Upw, p2.getUpw());
    }


    @Test
    void signInPost() {
        SignInPostReq req1 = new SignInPostReq();
        req1.setUid("id1");
        req1.setUpw("1212");
        String hashedUpw1 = BCrypt.hashpw(req1.getUpw(), BCrypt.gensalt());

        SignInPostReq req2 = new SignInPostReq();
        req2.setUid("id2");
        req2.setUpw("2323");
        String hashedUpw2 = BCrypt.hashpw(req2.getUpw(), BCrypt.gensalt());

        User user1 = new User(10, req1.getUid(), hashedUpw1, "홍길동1", "사진1.jpg", null, null);
        given(mapper.signInPost(req1.getUid())).willReturn(user1);

        User user2 = new User(20, req2.getUid(), hashedUpw2, "홍길동2", "사진2.jpg", null, null);
        given(mapper.signInPost(req2.getUid())).willReturn(user2);


        try(MockedStatic<BCrypt> mockedStatic = mockStatic(BCrypt.class)) {

            mockedStatic.when(() -> BCrypt.checkpw(req1.getUpw(), user1.getUpw())).thenReturn(true);
            mockedStatic.when(() -> BCrypt.checkpw(req2.getUpw(), user2.getUpw())).thenReturn(true);

            SignInPostRes res1 = service.signInPost(req1);
            assertEquals(user1.getUserId(), res1.getUserId(), "1. userId 다름");
            assertEquals(user1.getNm(), res1.getNm(), "1. nm 다름");
            assertEquals(user1.getPic(), res1.getPic(), "1. pic 다름");

            mockedStatic.verify(() -> BCrypt.checkpw(req1.getUpw(), user1.getUpw()));

            SignInPostRes res2 = service.signInPost(req2);
            assertEquals(user2.getUserId(), res2.getUserId(), "2. userId 다름");
            assertEquals(user2.getNm(), res2.getNm(), "2. nm 다름");
            assertEquals(user2.getPic(), res2.getPic(), "2. pic 다름");

            mockedStatic.verify(() -> BCrypt.checkpw(req2.getUpw(), user2.getUpw()));
        }



    }

    @Test
    void getUserInfo() {
    }

    @Test
    void patchProfilePic() {
    }
}