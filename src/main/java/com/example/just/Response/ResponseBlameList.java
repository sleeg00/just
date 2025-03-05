package com.example.just.Response;


import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseBlameList {

    private int blameCount;

    private List<ResponseBlameDto> blameList;

}
