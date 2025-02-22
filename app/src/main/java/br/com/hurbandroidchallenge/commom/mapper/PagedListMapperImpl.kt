package br.com.hurbandroidchallenge.commom.mapper

import br.com.hurbandroidchallenge.data.remote.model.base.PagedListResponse
import br.com.hurbandroidchallenge.domain.model.PagedList

class PagedListMapperImpl<I, O>(
    private val listMapper: NullableListMapper<I, O>
) : PagedListMapper<I, O> {

    override fun map(input: PagedListResponse<I>) = input.run {
            PagedList(
                next = next,
                previous = previous,
                results = listMapper.map(results)
            )
        }

}