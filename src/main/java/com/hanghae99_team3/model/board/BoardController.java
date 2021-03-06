package com.hanghae99_team3.model.board;


import com.hanghae99_team3.login.jwt.PrincipalDetails;
import com.hanghae99_team3.model.board.dto.request.BoardRequestDto;
import com.hanghae99_team3.model.board.dto.response.BoardDetailResponseDto;
import com.hanghae99_team3.model.board.dto.response.BoardResponseDto;
import com.hanghae99_team3.model.board.service.BoardDocumentService;
import com.hanghae99_team3.model.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardDocumentService boardDocumentService;

    @GetMapping("/api/board/{boardId}")
    public BoardDetailResponseDto getOneBoard(@PathVariable Long boardId) {
        return new BoardDetailResponseDto(boardService.findBoardById(boardId));
    }

    @GetMapping("/api/boards/preview")
    public List<BoardResponseDto> getBoardsBySortPreview(@RequestParam String entityName){
        return boardService.getBoardsBySortPreview(entityName).stream()
                .map(BoardResponseDto::new).collect(Collectors.toList());
    }

    @GetMapping("/api/boards")
    public Page<BoardResponseDto> getAllBoardsBySort(Pageable pageable) {
        return boardService.getAllBoardsBySort(pageable).map(BoardResponseDto::new);
    }

    @GetMapping("/api/boards/resource")
    public Page<BoardResponseDto> getBoardDocumentsByResource(@RequestParam String resourceName,
                                                              Pageable pageable){
        return boardDocumentService.getBoardDocumentsByResource(resourceName,pageable).map(BoardResponseDto::new);
    }

    @GetMapping("/api/boards/title")
    public Page<BoardResponseDto> getBoardDocumentsByTitle(@RequestParam String titleWords,
                                                           Pageable pageable){
        return boardDocumentService.getBoardDocumentsByTitle(titleWords,pageable).map(BoardResponseDto::new);
    }

    @GetMapping("/api/board/resource/recommend")
    public Map<String, List<String>> recommendResource(@RequestParam String resourceName){
        Map<String, List<String>> result = new HashMap<>();
        result.put("resourceRecommendList",
                boardDocumentService.recommendResource(resourceName)
        );
        return result;
    }

    @GetMapping("/api/board/title/recommend")
    public Map<String, List<String>> recommendBoardDocumentByTitle(@RequestParam String titleWords){
        Map<String, List<String>> result = new HashMap<>();
        result.put("titleRecommendList",
            boardDocumentService.recommendBoardDocumentByTitle(titleWords)
        );
        return result;
    }


//    @GetMapping("/api/boards/resource")
//    public Page<BoardResponseDto> getByResource(@RequestParam String searchWord,
//                                                Pageable pageable
//                                                ){
//        return boardService.getByResource(searchWord,pageable).map(BoardResponseDto::new);
//    }

    @PostMapping("/api/board/image")
    public Map<String, String> createImage(@RequestPart MultipartFile multipartFile,
                                           @RequestParam Long boardId){

        Map<String, String> result = new HashMap<>();
        result.put("imageLink",
                boardService.createImage(multipartFile, boardId)
        );
        return result;
    }

    @GetMapping("/api/board/check")
    public BoardDetailResponseDto checkModifyingBoard(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        return new BoardDetailResponseDto(
                boardService.checkModifyingBoard(principalDetails)
        );
    }

    @PutMapping("/api/board/temp")
    public void createTempBoard(@AuthenticationPrincipal PrincipalDetails principalDetails,
                            @RequestParam Long boardId,
                            @RequestPart BoardRequestDto boardRequestDto){

        boardService.createTempBoard(principalDetails,boardId,boardRequestDto);
    }

    @PostMapping("/api/board")
    public void createBoard(@AuthenticationPrincipal PrincipalDetails principalDetails,
                            @RequestParam Long boardId,
                            @RequestPart BoardRequestDto boardRequestDto){

        boardService.createBoard(principalDetails,boardId,boardRequestDto);
    }

    @PutMapping("/api/board")
    public void updateBoard(@AuthenticationPrincipal PrincipalDetails principalDetails,
                            @RequestParam Long boardId,
                            @RequestPart BoardRequestDto boardRequestDto){

        boardService.updateBoard(principalDetails,boardId,boardRequestDto);
    }

    @DeleteMapping("/api/board")
    public void deleteBoard(@AuthenticationPrincipal PrincipalDetails principalDetails,
                            @RequestParam Long boardId) {

        boardService.deleteBoard(principalDetails, boardId);
    }


//
//    @PostMapping(value = "/api/board/step/1")
//    public Map<String, Long> createBoardStepMain(@RequestPart BoardRequestDtoStepMain boardRequestDtoStepMain,
//                                                 @RequestPart MultipartFile multipartFile,
//                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        Map<String, Long> result = new HashMap<>();
//        result.put("boardId",
//                boardService.createBoardStepMain(boardRequestDtoStepMain, multipartFile, principalDetails)
//        );
//        return result;
//    }
//
//    @PostMapping(value = "/api/board/step/2")
//    public Map<String, Long> createBoardStepResource(@RequestPart BoardRequestDtoStepResource boardRequestDtoStepResource,
//                                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {
//
//        Map<String, Long> result = new HashMap<>();
//        result.put("boardId",
//                boardService.createBoardStepResource(boardRequestDtoStepResource, principalDetails)
//        );
//        return result;
//
//    }
//
//    @PostMapping(value = "/api/board/step/3")
//    public Map<String, Long> createBoardStepRecipe(@RequestPart BoardRequestDtoStepRecipe boardRequestDtoStepRecipe,
//                                                   @RequestPart MultipartFile multipartFile,
//                                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
//
//        Map<String, Long> result = new HashMap<>();
//        result.put("boardId",
//                boardService.createBoardStepRecipe(boardRequestDtoStepRecipe, multipartFile, principalDetails)
//        );
//        return result;
//
//    }
//
//    @PostMapping(value = "/api/board/step/-1")
//    public void createBoardStepEnd(@RequestPart Long boardId,
//                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
//
//        boardService.createBoardStepEnd(boardId, principalDetails);
//
//    }
//
//    @PutMapping("/api/board/step/1")
//    public Map<String, Long> updateBoardStepMain(@RequestPart Long boardId,
//                                                 @RequestPart BoardRequestDtoStepMain boardRequestDtoStepMain,
//                                                 @RequestPart MultipartFile multipartFile,
//                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
//
//        Map<String, Long> result = new HashMap<>();
//        result.put("boardId",
//                boardService.updateBoardStepMain(boardId, boardRequestDtoStepMain, multipartFile, principalDetails)
//        );
//        return result;
//
//    }
//
//    @PutMapping("/api/board/step/2")
//    public Map<String, Long> updateBoardStepResource(@RequestPart BoardRequestDtoStepResource boardRequestDtoStepResource,
//                                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {
//
//        Map<String, Long> result = new HashMap<>();
//        result.put("boardId",
//                boardService.updateBoardStepResource(boardRequestDtoStepResource, principalDetails)
//        );
//        return result;
//    }
//
//    @PutMapping("/api/board/step/3")
//    public Map<String, Long> updateBoardStepRecipe(@RequestPart BoardRequestDtoStepRecipe boardRequestDtoStepRecipe,
//                                                   @RequestPart MultipartFile multipartFile,
//                                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
//
//        Map<String, Long> result = new HashMap<>();
//        result.put("boardId",
//                boardService.updateBoardStepRecipe(boardRequestDtoStepRecipe, multipartFile, principalDetails)
//        );
//        return result;
//    }
//
//    @DeleteMapping("/api/board/step/3")
//    public void deleteRecipeStep(@RequestPart Long boardId,
//                                 @RequestPart Integer stepNum,
//                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
//
//        boardService.deleteRecipeStep(boardId, stepNum, principalDetails);
//    }
//

}