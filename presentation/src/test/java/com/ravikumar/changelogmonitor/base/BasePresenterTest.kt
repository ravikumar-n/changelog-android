package com.ravikumar.changelogmonitor.base

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class BasePresenterTest {
  @Mock private lateinit var view: MvpView

  private lateinit var presenter: BasePresenter<MvpView>

  @Before
  fun setUp() {
    presenter = BasePresenter()
    presenter.onAttach(view)
  }

  @Test
  fun `Dispose the disposables in the presenter on detach`() {
    assertFalse { presenter.disposable.isDisposed }

    presenter.onDetach()

    assertTrue { presenter.disposable.isDisposed }
  }

  @Test
  fun `Set view to null on detach`() {
    assertNotNull(presenter.view)

    presenter.onDetach()

    assertNull(presenter.view)
  }
}
