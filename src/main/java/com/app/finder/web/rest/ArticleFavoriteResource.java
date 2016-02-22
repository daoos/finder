package com.app.finder.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.app.finder.domain.ArticleFavorite;
import com.app.finder.service.ArticleFavoriteService;
import com.app.finder.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ArticleFavorite.
 * 文章收藏
 */
@RestController
@RequestMapping("/api")
public class ArticleFavoriteResource {

    private final Logger log = LoggerFactory.getLogger(ArticleFavoriteResource.class);
        
    @Inject
    private ArticleFavoriteService articleFavoriteService;
    
    /**
     * POST  /articleFavorites -> Create a new articleFavorite.
     */
    @RequestMapping(value = "/articleFavorites",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ArticleFavorite> createArticleFavorite(@RequestBody ArticleFavorite articleFavorite) throws URISyntaxException {
        log.debug("REST request to save ArticleFavorite : {}", articleFavorite);
        if (articleFavorite.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("articleFavorite", "idexists", "A new articleFavorite cannot already have an ID")).body(null);
        }
        ArticleFavorite result = articleFavoriteService.save(articleFavorite);
        return ResponseEntity.created(new URI("/api/articleFavorites/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("articleFavorite", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /articleFavorites -> Updates an existing articleFavorite.
     */
    @RequestMapping(value = "/articleFavorites",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ArticleFavorite> updateArticleFavorite(@RequestBody ArticleFavorite articleFavorite) throws URISyntaxException {
        log.debug("REST request to update ArticleFavorite : {}", articleFavorite);
        if (articleFavorite.getId() == null) {
            return createArticleFavorite(articleFavorite);
        }
        ArticleFavorite result = articleFavoriteService.save(articleFavorite);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("articleFavorite", articleFavorite.getId().toString()))
            .body(result);
    }

    /**
     * GET  /articleFavorites -> get all the articleFavorites.
     */
    @RequestMapping(value = "/articleFavorites",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ArticleFavorite> getAllArticleFavorites() {
        log.debug("REST request to get all ArticleFavorites");
        return articleFavoriteService.findAll();
            }

    /**
     * GET  /articleFavorites/:id -> get the "id" articleFavorite.
     */
    @RequestMapping(value = "/articleFavorites/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ArticleFavorite> getArticleFavorite(@PathVariable Long id) {
        log.debug("REST request to get ArticleFavorite : {}", id);
        ArticleFavorite articleFavorite = articleFavoriteService.findOne(id);
        return Optional.ofNullable(articleFavorite)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /articleFavorites/:id -> delete the "id" articleFavorite.
     */
    @RequestMapping(value = "/articleFavorites/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteArticleFavorite(@PathVariable Long id) {
        log.debug("REST request to delete ArticleFavorite : {}", id);
        articleFavoriteService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("articleFavorite", id.toString())).build();
    }

}
