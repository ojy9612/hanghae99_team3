package com.hanghae99_team3.model.recipestep.dto;

import com.hanghae99_team3.model.recipestep.RecipeStep;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class RecipeStepResponseDto {

    private Integer stepNum;
    private String stepContent;
    private String imageLink;

    @Builder
    public RecipeStepResponseDto(RecipeStep recipeStep) {
        this.stepNum = recipeStep.getStepNum();
        this.stepContent = recipeStep.getContent();
        this.imageLink = recipeStep.getImageLink();
    }

}
