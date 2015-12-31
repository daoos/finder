package com.app.finder.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.app.finder.domain.ForbiddenWord;
import com.app.finder.repository.ForbiddenWordRepository;
import com.app.finder.repository.search.ForbiddenWordSearchRepository;
import com.app.finder.web.rest.util.HeaderUtil;
import com.app.finder.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * REST controller for managing ForbiddenWord.
 */
@RestController
@RequestMapping("/api")
public class ForbiddenWordResource {

    private final Logger log = LoggerFactory.getLogger(ForbiddenWordResource.class);
        
    @Inject
    private ForbiddenWordRepository forbiddenWordRepository;
    
    @Inject
    private ForbiddenWordSearchRepository forbiddenWordSearchRepository;
    
    /**
     * POST  /forbiddenWords -> Create a new forbiddenWord.
     */
    @RequestMapping(value = "/forbiddenWords",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ForbiddenWord> createForbiddenWord(@RequestBody ForbiddenWord forbiddenWord) throws URISyntaxException {
        log.debug("REST request to save ForbiddenWord : {}", forbiddenWord);
        if (forbiddenWord.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("forbiddenWord", "idexists", "A new forbiddenWord cannot already have an ID")).body(null);
        }
        ForbiddenWord result = forbiddenWordRepository.save(forbiddenWord);
        forbiddenWordSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/forbiddenWords/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("forbiddenWord", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /forbiddenWords -> Updates an existing forbiddenWord.
     */
    @RequestMapping(value = "/forbiddenWords",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ForbiddenWord> updateForbiddenWord(@RequestBody ForbiddenWord forbiddenWord) throws URISyntaxException {
        log.debug("REST request to update ForbiddenWord : {}", forbiddenWord);
        if (forbiddenWord.getId() == null) {
            return createForbiddenWord(forbiddenWord);
        }
        ForbiddenWord result = forbiddenWordRepository.save(forbiddenWord);
        forbiddenWordSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("forbiddenWord", forbiddenWord.getId().toString()))
            .body(result);
    }

    /**
     * GET  /forbiddenWords -> get all the forbiddenWords.
     */
    @RequestMapping(value = "/forbiddenWords",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ForbiddenWord>> getAllForbiddenWords(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ForbiddenWords");
        Page<ForbiddenWord> page = forbiddenWordRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/forbiddenWords");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /forbiddenWords/:id -> get the "id" forbiddenWord.
     */
    @RequestMapping(value = "/forbiddenWords/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ForbiddenWord> getForbiddenWord(@PathVariable Long id) {
        log.debug("REST request to get ForbiddenWord : {}", id);
        ForbiddenWord forbiddenWord = forbiddenWordRepository.findOne(id);
        return Optional.ofNullable(forbiddenWord)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /forbiddenWords/:id -> delete the "id" forbiddenWord.
     */
    @RequestMapping(value = "/forbiddenWords/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteForbiddenWord(@PathVariable Long id) {
        log.debug("REST request to delete ForbiddenWord : {}", id);
        forbiddenWordRepository.delete(id);
        forbiddenWordSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("forbiddenWord", id.toString())).build();
    }

    /**
     * SEARCH  /_search/forbiddenWords/:query -> search for the forbiddenWord corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/forbiddenWords/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ForbiddenWord> searchForbiddenWords(@PathVariable String query) {
        log.debug("REST request to search ForbiddenWords for query {}", query);
        return StreamSupport
            .stream(forbiddenWordSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}