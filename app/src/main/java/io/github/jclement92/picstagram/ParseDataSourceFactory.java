package io.github.jclement92.picstagram;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import io.github.jclement92.picstagram.model.Post;

public class ParseDataSourceFactory extends DataSource.Factory<Integer, Post> {

    MutableLiveData<ParsePositionalDataSource> mutableLiveData;
    ParsePositionalDataSource dataSource;

    public ParseDataSourceFactory() {
        this.mutableLiveData = new MutableLiveData<>();
    }

    @NonNull
    @Override
    public DataSource<Integer, Post> create() {
        dataSource = new ParsePositionalDataSource();
        mutableLiveData.postValue(dataSource);
        return dataSource;
    }

    public MutableLiveData<ParsePositionalDataSource> getMutableLiveData() {
        return mutableLiveData;
    }

}
