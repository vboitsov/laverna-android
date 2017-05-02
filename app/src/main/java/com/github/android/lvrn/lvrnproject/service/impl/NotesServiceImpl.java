package com.github.android.lvrn.lvrnproject.service.impl;

import com.github.android.lvrn.lvrnproject.persistent.entity.impl.Note;
import com.github.android.lvrn.lvrnproject.persistent.entity.impl.Notebook;
import com.github.android.lvrn.lvrnproject.persistent.entity.impl.Tag;
import com.github.android.lvrn.lvrnproject.persistent.repository.NotesRepository;
import com.github.android.lvrn.lvrnproject.service.NotebooksService;
import com.github.android.lvrn.lvrnproject.service.NotesService;
import com.github.android.lvrn.lvrnproject.service.ProfilesService;
import com.github.android.lvrn.lvrnproject.service.TagsService;
import com.github.android.lvrn.lvrnproject.service.TasksService;
import com.github.android.lvrn.lvrnproject.service.core.impl.ProfileDependedServiceImpl;
import com.github.android.lvrn.lvrnproject.service.util.NoteTextParser;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * @author Vadim Boitsov <vadimboitsov1@gmail.com>
 */

public class NotesServiceImpl extends ProfileDependedServiceImpl<Note> implements NotesService {

    private final NotesRepository mNotesRepository;

    private final TasksService mTasksService;

    private final TagsService mTagsService;

    private final NotebooksService mNotebooksService;

    @Inject
    public NotesServiceImpl(NotesRepository notesRepository,
                            TasksService tasksService,
                            TagsService tagsService,
                            ProfilesService profilesService,
                            NotebooksService notebooksService) {

        super(notesRepository, profilesService);
        mNotesRepository = notesRepository;
        mTasksService = tasksService;
        mTagsService = tagsService;
        mNotebooksService = notebooksService;
    }

    /**
     * @param profileId
     * @param notebookId
     * @param title
     * @param content
     * @param isFavorite
     * @throws IllegalArgumentException
     */
    @Override
    public void create(String profileId,
                       String notebookId,
                       String title,
                       String content,
                       boolean isFavorite) {

        validate(profileId, notebookId, title);

        String noteId = UUID.randomUUID().toString();

        mNotesRepository.add(new Note(
                noteId,
                profileId,
                notebookId,
                title,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                content,
                isFavorite));

        parseTasksAndTags(profileId, noteId, content);
    }

    /**
     * @param profileId
     * @param noteId
     * @param content
     * @throws IllegalArgumentException
     */
    private void parseTasksAndTags(String profileId, String noteId, String content) {
        NoteTextParser.parseTasks(content)
                .forEach((description, status) ->
                        mTasksService.create(profileId, noteId, description, status));

        NoteTextParser.parseTags(content)
                .forEach(tagName -> mTagsService.create(profileId, tagName));
    }

    @Override
    public List<Note> getByNotebook(Notebook notebook, int from, int amount) {
        return mNotesRepository.getByNotebook(notebook, from, amount);
    }

    @Override
    public List<Note> getByTag(Tag tag, int from, int amount) {
        return mNotesRepository.getByTag(tag, from, amount);
    }

    /**
     * @param entity to update.
     * @throws IllegalArgumentException
     */
    @Override
    public void update(Note entity) {
        validate(entity.getProfileId(), entity.getProfileId(), entity.getTitle());
        mNotesRepository.update(entity);
    }

    /**
     * @param profileId
     * @param notebookId
     * @param title
     * @throws IllegalArgumentException
     */
    private void validate(String profileId, String notebookId, String title) {
        super.checkProfileExistence(profileId);
        checkNotebookExistence(notebookId);
        super.checkName(title);
    }

    /**
     * @param notebookId
     * @throws IllegalArgumentException
     */
    private void checkNotebookExistence(String notebookId) {
        if (notebookId != null && !mNotebooksService.getById(notebookId).isPresent()) {
            throw new IllegalArgumentException("The notebook is not found!");
        }
    }
}
