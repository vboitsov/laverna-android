package com.github.android.lvrn.lvrnproject.service.extension;

import com.github.android.lvrn.lvrnproject.BuildConfig;
import com.github.android.lvrn.lvrnproject.persistent.database.DatabaseManager;
import com.github.android.lvrn.lvrnproject.persistent.entity.Profile;
import com.github.android.lvrn.lvrnproject.persistent.repository.extension.impl.NotebookRepositoryImpl;
import com.github.android.lvrn.lvrnproject.persistent.repository.extension.impl.ProfileRepositoryImpl;
import com.github.android.lvrn.lvrnproject.service.extension.impl.NotebookServiceImpl;
import com.github.android.lvrn.lvrnproject.service.extension.impl.ProfileServiceImpl;
import com.github.android.lvrn.lvrnproject.service.form.NotebookForm;
import com.github.android.lvrn.lvrnproject.service.form.ProfileForm;
import com.google.common.base.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Vadim Boitsov <vadimboitsov1@gmail.com>
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NotebookServiceImplTest {

    private NotebookService notebookService;

    private Profile profile;

    @Before
    public void setUp() {
        DatabaseManager.initializeInstance(RuntimeEnvironment.application);

        ProfileService profileService = new ProfileServiceImpl(new ProfileRepositoryImpl());
        profileService.openConnection();
        profileService.create(new ProfileForm("Temp profile"));
        profile = profileService.getAll().get(0);
        profileService.closeConnection();

        notebookService = new NotebookServiceImpl(new NotebookRepositoryImpl(), profileService);
        notebookService.openConnection();
    }


    @Test
    public void serviceShouldCreateNotebook() {
        assertThat(notebookService.create(new NotebookForm(profile.getId(), null, "new notebook"))
                .isPresent())
                .isTrue();
    }

    @Test
    public void serviceShouldNotCreateNotebook() {
        assertThat(notebookService.create(new NotebookForm(null, null, "new notebook"))
                .isPresent())
                .isFalse();

        assertThat(notebookService.create(new NotebookForm(profile.getId(), null, ""))
                .isPresent())
                .isFalse();

        assertThat(notebookService.create(new NotebookForm(profile.getId(), "strang_id", "ddffdf"))
                .isPresent())
                .isFalse();
    }

    @Test
    public void serviceShouldUpdateEntity() {
        Optional<String> notebookIdOptional = notebookService.create(new NotebookForm(profile.getId(), null, "new notebook"));
        assertThat(notebookIdOptional.isPresent()).isTrue();

        assertThat(notebookService.update(notebookIdOptional.get(), new NotebookForm(null, null, "New name"))).isTrue();
    }

    @Test
    public void serviceShouldNotUpdateEntity() {
        Optional<String> notebookIdOptional = notebookService.create(new NotebookForm(profile.getId(), null, "new notebook"));
        assertThat(notebookIdOptional.isPresent()).isTrue();

        assertThat(notebookService.update(notebookIdOptional.get(), new NotebookForm(null, null, ""))).isFalse();
    }

    @Test
    public void serviceShouldGetEntityByName() {
        assertThat(notebookService.create(new NotebookForm(profile.getId(), null, "new notebook"))
                .isPresent())
                .isTrue();

        assertThat(notebookService.getByName(profile.getId(), "new", 1, 100)).hasSize(1);
    }

    @After
    public void finish() {
        notebookService.closeConnection();
    }
}