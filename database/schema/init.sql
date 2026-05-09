create table mst_user (
    id bigserial primary key,
    full_name varchar(100) not null,
    username varchar(50) not null UNIQUE,
    password varchar(255) not null,
    role varchar(20) not null,
    is_active boolean default true,
    created_by varchar(60) not null,
    created_at timestamp not null,
    updated_by varchar(60) default null,
    updated_at timestamp default null
);

create table mst_category (
    id bigserial primary key,
    name varchar(100) not null,
    description text,
    created_by varchar(60) not null,
    created_at timestamp not null,
    updated_by varchar(60) default null,
    updated_at timestamp default null
);

create table mst_product (
    id bigserial primary key,
    category_id bigint not null REFERENCES mst_category(id),
    sku VARCHAR(50) not null UNIQUE,
    name varchar(150) not null,
    price numeric(18,2) not null,
    stock BIGINT DEFAULT 0,
    description text,
    is_active boolean default true,
    created_by varchar(60) not null,
    created_at timestamp not null,
    updated_by varchar(60) default null,
    updated_at timestamp default null
);

create table trn_transaction (
    id bigserial primary key,
    invoice_no varchar(60) not null UNIQUE,
    total_amount numeric(18,2) not null,
    transaction_date timestamp not null,
    created_by varchar(60) not null,
    created_at timestamp not null,
    updated_by varchar(60) default null,
    updated_at timestamp default null
);

create table trn_transaction_item (
    id bigserial primary key,
    transaction_id bigint not null REFERENCES trn_transaction(id),
    product_id bigint not null REFERENCES mst_product(id),
    quantity bigint not null,
    price numeric(18,2) not null,
    sub_total numeric(18,2) not null,
    created_by varchar(60) not null,
    created_at timestamp not null,
    updated_by varchar(60) default null,
    updated_at timestamp default null
);

create table trn_stock_movement (
    id bigserial primary key,
    product_id bigint not null REFERENCES mst_product(id),
    type varchar(20) not null, -- IN or OUT
    quantity bigint not null,
    notes text,
    created_by varchar(60) not null,
    created_at timestamp not null,
    updated_by varchar(60) default null,
    updated_at timestamp default null
);