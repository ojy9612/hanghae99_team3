package com.hanghae99_team3.model.resource.service;

import com.hanghae99_team3.model.board.domain.Board;
import com.hanghae99_team3.model.resource.domain.ResourceKeywordDocument;
import com.hanghae99_team3.model.resource.repository.ResourceRepository;
import com.hanghae99_team3.model.resource.domain.Resource;
import com.hanghae99_team3.model.resource.dto.ResourceRequestDto;
import com.hanghae99_team3.model.resource.repository.ResourceSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceSearchRepository resourceSearchRepository;

    public void createResource(List<ResourceRequestDto> resourceRequestDtoList, Board board) {

        resourceRequestDtoList.forEach(resourceRequestDto -> {
            Resource resource = Resource.builder()
                    .resourceRequestDto(resourceRequestDto)
                    .board(board)
                    .build();

            Optional<ResourceKeywordDocument> optionalResourceKeywordDocument =
                    resourceSearchRepository.findByResourceNameKeyword(resource.getResourceName());

            if (optionalResourceKeywordDocument.isPresent()) {
                ResourceKeywordDocument resourceKeywordDocument = optionalResourceKeywordDocument.get();
                resourceKeywordDocument.plusCnt();
                resourceSearchRepository.save(resourceKeywordDocument);
            } else {
                resourceSearchRepository.save(ResourceKeywordDocument.builder()
                        .resource(resource)
                        .build()
                );
            }

            resourceRepository.save(resource);
        });
    }

    public void updateResource(List<ResourceRequestDto> resourceRequestDtoList, Board board) {
        this.removeAllResource(board);
        this.createResource(resourceRequestDtoList, board);
    }

    public void removeAllResource(Board board) {
        List<String> resourceNameList = board.getResourceList().stream()
                .map(Resource::getResourceName).collect(Collectors.toList());

        resourceNameList.forEach(s -> {
            resourceSearchRepository.findByResourceNameKeyword(s).ifPresent(resourceKeywordDocument -> {
                        resourceKeywordDocument.minusCnt();
                        if (resourceKeywordDocument.getCnt() <= 0) {
                            resourceSearchRepository.delete(resourceKeywordDocument);
                        }
                    }
            );
        });

        resourceRepository.deleteAll(resourceRepository.findAllByBoard(board));
    }

}
