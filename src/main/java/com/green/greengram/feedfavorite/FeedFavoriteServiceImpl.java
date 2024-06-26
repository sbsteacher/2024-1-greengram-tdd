package com.green.greengram.feedfavorite;

import com.green.greengram.feedfavorite.model.FeedFavoriteReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedFavoriteServiceImpl implements FeedFavoriteService {
    private final FeedFavoriteMapper mapper;

    public int toggleReq(FeedFavoriteReq p) {
        int result = mapper.delFeedFavorite(p);
        if(result == 1) {
            return 0;
        }
        return mapper.insFeedFavorite(p);
    }

}
