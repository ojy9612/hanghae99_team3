package com.hanghae99_team3.model.images;

import com.hanghae99_team3.model.board.domain.Board;
import com.hanghae99_team3.model.s3.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImagesService {

    private final ImagesRepository imagesRepository;
    private final AwsS3Service awsS3Service;

    public String createImages(MultipartFile multipartFile, Board board) {

        String imgLink = awsS3Service.uploadFile(multipartFile);
        if (!imgLink.equals("")) {
            Images images = Images.builder()
                    .imageLink(imgLink)
                    .board(board)
                    .build();

            return imagesRepository.save(images).getImageLink();
        }
        throw new IllegalArgumentException("올바른 이미지파일이 아닙니다.");
    }

    public void removeImages(Board board) {
        List<Images> imagesList = imagesRepository.findAllByBoard(board);

        imagesList.forEach(images -> awsS3Service.deleteFile(images.getImageLink()));

        imagesRepository.deleteAll(imagesList);
    }
}
