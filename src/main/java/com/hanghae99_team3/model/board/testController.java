package com.hanghae99_team3.model.board;

import com.hanghae99_team3.login.jwt.PrincipalDetails;
import com.hanghae99_team3.model.board.domain.Board;
import com.hanghae99_team3.model.board.domain.BoardDocument;
import com.hanghae99_team3.model.board.dto.response.BoardResponseDto;
import com.hanghae99_team3.model.board.repository.BoardRepository;
import com.hanghae99_team3.model.board.repository.BoardSearchRepository;
import com.hanghae99_team3.model.board.service.BoardDocumentService;
import com.hanghae99_team3.model.recipestep.RecipeStepService;
import com.hanghae99_team3.model.resource.domain.Resource;
import com.hanghae99_team3.model.resource.domain.ResourceKeywordDocument;
import com.hanghae99_team3.model.resource.dto.ResourceRequestDto;
import com.hanghae99_team3.model.resource.repository.ResourceSearchRepository;
import com.hanghae99_team3.model.resource.service.ResourceService;
import com.hanghae99_team3.model.user.UserService;
import com.hanghae99_team3.model.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class testController {

    private final BoardSearchRepository boardSearchRepository;
    private final BoardRepository boardRepository;
    private final BoardDocumentService boardDocumentService;
    private final ResourceSearchRepository resourceSearchRepository;
    private final UserService userService;
    private final ResourceService resourceService;
    private final RecipeStepService recipeStepService;


    @GetMapping("/api/boards/elastic")
    public Page<BoardResponseDto> getAllBoardDocument(Pageable pageable){
        List<Long> boardIdList = boardSearchRepository.findFirst2By().stream()
                .map(BoardDocument::getId).collect(Collectors.toList());

        return boardRepository.findAllByIdIn(boardIdList, pageable).map(BoardResponseDto::new);
    }
    @GetMapping("/api/resources/elastic")
    public List<ResourceKeywordDocument> getAllResourceKeywordDocument(Pageable pageable){

        return resourceSearchRepository.findAll();
    }

    @PostMapping("/test/resources")
    public void createManyResources(@RequestBody TestWrapper resources){
        List<ResourceKeywordDocument> resourceKeywordDocumentList = new ArrayList<>();

        resources.getResources().forEach(resource -> {
            Resource resource1 = new Resource (
                    ResourceRequestDto.builder().resourceName(resource).amount("1").category("????????????").build()
            );

            ResourceKeywordDocument resourceKeywordDocument = ResourceKeywordDocument.builder()
                    .resource(resource1)
                    .build();
            resourceKeywordDocument.plusCnt();
            resourceKeywordDocument.plusCnt();
            resourceKeywordDocument.plusCnt();
            resourceKeywordDocument.plusCnt();
            resourceKeywordDocumentList.add(resourceKeywordDocument);
        });

        resourceSearchRepository.saveAll(resourceKeywordDocumentList);
    }

    @PostMapping("/test/boards111")
    @Transactional
    public void createManyBoards(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                 @RequestBody TestBoardDtoList testBoardDtoList){
        User user = userService.findUserByAuthEmail(principalDetails);

        testBoardDtoList.getBoardRequestDtoList().forEach(boardRequestDto -> {
            Board board = Board.builder()
                    .boardRequestDto(boardRequestDto)
                    .user(user)
                    .build();

            resourceService.createResource(boardRequestDto.getResourceRequestDtoList(), board);
            recipeStepService.createRecipeStep(boardRequestDto.getRecipeStepRequestDtoList(),board);

            boardDocumentService.createBoard(board);
            boardRepository.save(board);
        });

    }

}

