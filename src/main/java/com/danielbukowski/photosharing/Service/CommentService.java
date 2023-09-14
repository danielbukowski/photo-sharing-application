package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.CommentDto;
import com.danielbukowski.photosharing.Dto.NewCommentRequest;
import com.danielbukowski.photosharing.Dto.SimplePageResponse;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.Comment;
import com.danielbukowski.photosharing.Exception.ImageNotFoundException;
import com.danielbukowski.photosharing.Mapper.CommentMapper;
import com.danielbukowski.photosharing.Repository.CommentRepository;
import com.danielbukowski.photosharing.Repository.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.danielbukowski.photosharing.Enum.ExceptionMessageResponse.IMAGE_NOT_FOUND;

@Service
@AllArgsConstructor
@Slf4j
public class CommentService {

    private static final int PAGE_SIZE = 20;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public Long saveCommentToImage(NewCommentRequest newCommentRequest, UUID imageId, Account account) {
        var image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException(IMAGE_NOT_FOUND.getMessage()));

        if (image.isPrivate() && !image.getAccount().equals(account))
            throw new ImageNotFoundException(IMAGE_NOT_FOUND.getMessage());

        Comment comment = commentRepository.save(Comment.builder()
                .account(account)
                .image(image)
                .content(newCommentRequest.content())
                .build());

        return comment.getId();
    }

    public SimplePageResponse<CommentDto> getCommentsFromImage(UUID imageId, Integer pageNumber, Account account) {
        var expectedImageInDb = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException(IMAGE_NOT_FOUND.getMessage()));

        if (!expectedImageInDb.getAccount().equals(account))
            throw new ImageNotFoundException(IMAGE_NOT_FOUND.getMessage());

        var pageOfComments = commentRepository.getByImageId(PageRequest.of(pageNumber, PAGE_SIZE), imageId);
        return new SimplePageResponse<>(
                pageOfComments.getNumberOfElements(),
                pageOfComments
                        .getContent()
                        .stream()
                        .map(commentMapper::fromCommentToCommentDto)
                        .toList(),
                pageOfComments.getNumber(),
                pageOfComments.getTotalPages(),
                pageOfComments.isLast()
        );
    }

}
