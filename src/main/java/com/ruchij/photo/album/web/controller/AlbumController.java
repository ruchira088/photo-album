package com.ruchij.photo.album.web.controller;

import com.ruchij.photo.album.dao.album.Album;
import com.ruchij.photo.album.services.album.AlbumService;
import com.ruchij.photo.album.web.controller.requests.CreateAlbumRequest;
import com.ruchij.photo.album.web.controller.responses.AlbumResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/album")
public class AlbumController {
  private final AlbumService albumService;

  public AlbumController(AlbumService albumService) {
	this.albumService = albumService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AlbumResponse> create(@RequestBody CreateAlbumRequest createAlbumRequest) {
    Album album = albumService.create("user-id", createAlbumRequest.name(), createAlbumRequest.description());
    return ResponseEntity.status(HttpStatus.CREATED).body(AlbumResponse.from(album));
  }

  @ResponseBody
  @GetMapping(path = "id/{albumId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public AlbumResponse findById(@PathVariable String albumId) {
    return albumService.findById(albumId).map(AlbumResponse::from).orElseThrow();
  }

}
