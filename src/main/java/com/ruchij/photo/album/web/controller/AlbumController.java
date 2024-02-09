package com.ruchij.photo.album.web.controller;

import com.ruchij.photo.album.web.controller.requests.CreateAlbumRequest;
import com.ruchij.photo.album.web.controller.responses.AlbumResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/album")
public class AlbumController {

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AlbumResponse> create(@RequestBody CreateAlbumRequest createAlbumRequest) {
    return null;
  }

}
