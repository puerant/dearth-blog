package com.dearblog.service;

import com.dearblog.common.exception.BusinessException;
import com.dearblog.common.result.ResultCode;
import com.dearblog.dto.response.VisitSummaryVO;
import com.dearblog.dto.response.VisitTrendVO;
import com.dearblog.dto.response.ArticleRankVO;
import com.dearblog.entity.BlogArticle;
import com.dearblog.entity.BlogVisitLog;
import com.dearblog.mapper.BlogArticleMapper;
import com.dearblog.mapper.BlogVisitLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final BlogVisitLogMapper visitLogMapper;
    private final BlogArticleMapper articleMapper;

    public void recordVisit(String path, Long articleId, String ip, String userAgent, String referer) {
        BlogVisitLog log = new BlogVisitLog();
        log.setPath(path);
        log.setArticleId(articleId);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        log.setReferer(referer);
        log.setVisitDate(LocalDate.now());
        visitLogMapper.insert(log);

        if (articleId != null) {
            articleMapper.update(null, new LambdaUpdateWrapper<BlogArticle>()
                    .setSql("view_count = view_count + 1")
                    .eq(BlogArticle::getId, articleId));
        }
    }

    public VisitSummaryVO getSummary() {
        VisitSummaryVO vo = new VisitSummaryVO();
        vo.setTotalPv(visitLogMapper.countTotalPv());
        vo.setTotalUv(visitLogMapper.countTotalUv());
        vo.setTodayPv(visitLogMapper.countPvByDate(LocalDate.now()));
        vo.setTodayUv(visitLogMapper.countUvByDate(LocalDate.now()));
        return vo;
    }

    public List<VisitTrendVO> getTrend(int days) {
        LocalDate from = LocalDate.now().minusDays(days - 1);
        return visitLogMapper.selectTrendSince(from);
    }

    public List<ArticleRankVO> getArticleRank(int limit) {
        List<BlogArticle> articles = articleMapper.selectList(
                new LambdaQueryWrapper<BlogArticle>()
                        .select(BlogArticle::getId, BlogArticle::getTitle,
                                BlogArticle::getSlug, BlogArticle::getViewCount)
                        .eq(BlogArticle::getStatus, 2)
                        .orderByDesc(BlogArticle::getViewCount)
                        .last("LIMIT " + limit));
        return articles.stream().map(a -> {
            ArticleRankVO vo = new ArticleRankVO();
            vo.setId(a.getId());
            vo.setTitle(a.getTitle());
            vo.setSlug(a.getSlug());
            vo.setViewCount(a.getViewCount());
            return vo;
        }).collect(Collectors.toList());
    }
}
