package ru.practicum.shareit.request.repository;

import lombok.Generated;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Objects;

@Generated
public class OffsetLimitPageable implements Pageable, Serializable {

    private final int limit;
    private final int offset;
    private final Sort sort;

    public OffsetLimitPageable(int offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }

        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    public static OffsetLimitPageable of(int offset, int limit) {
        return new OffsetLimitPageable(offset, limit);
    }

    public OffsetLimitPageable(int offset, int limit, Sort.Direction direction, String... properties) {
        this(offset, limit, Sort.by(direction, properties));
    }

    public OffsetLimitPageable(int offset, int limit) {
        this(offset, limit, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    @NonNull
    public Sort getSort() {
        return sort;
    }

    @Override
    @NonNull
    public Pageable next() {
        return new OffsetLimitPageable((int) (getOffset() + getPageSize()), getPageSize(), getSort());
    }

    public OffsetLimitPageable previous() {
        return hasPrevious() ? new OffsetLimitPageable((int) (getOffset() - getPageSize()), getPageSize(),
                getSort()) : this;
    }


    @Override
    @NonNull
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    @NonNull
    public Pageable first() {
        return new OffsetLimitPageable(0, getPageSize(), getSort());
    }

    @Override
    @NonNull
    public Pageable withPage(int pageNumber) {
        return new OffsetLimitPageable(pageNumber, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffsetLimitPageable that = (OffsetLimitPageable) o;
        return limit == that.limit
                && offset == that.offset
                && Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit, offset, sort);
    }

    @Override
    public String toString() {
        return "OffsetLimitPageable{" +
                "limit=" + limit +
                ", offset=" + offset +
                ", sort=" + sort +
                '}';
    }
}
