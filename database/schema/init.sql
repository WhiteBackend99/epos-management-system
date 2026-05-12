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
    sku VARCHAR(100) not null UNIQUE,
    barcode varchar(100),
    name varchar(150) not null,
    description text,
    purchase_price numeric(18,2) default 0,
    selling_price numeric(18,2) default 0,
    stock BIGINT DEFAULT 0,
    min_stock bigint default 0,
    is_active boolean default true,
    is_deleted boolean default false,
    created_by varchar(60) not null,
    created_at timestamp not null,
    updated_by varchar(60) default null,
    updated_at timestamp default null,
    deleted_by varchar(60) default null,
    deleted_at timestamp default null
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

create table audit_trail (
    id bigserial PRIMARY KEY,
    references_id bigint default null,
    audit_type varchar(100) not null,
    action varchar(50),
    endpoint varchar(255),
    http_method varchar(20),
    request_payload jsonb,
    response_payload jsonb,
    error_payload jsonb,
    ip_address varchar(100),
    user_agent text,
    status bigint,
    created_by varchar(100),
    created_at timestamp default current_timestamp
);

CREATE INDEX idx_product_name ON mst_product(name);
CREATE INDEX idx_product_sku ON mst_product(sku);
CREATE INDEX idx_product_barcode ON mst_product(barcode);
CREATE INDEX idx_product_category ON mst_product(category_id);
CREATE INDEX idx_product_active ON mst_product(is_active);
CREATE INDEX idx_product_deleted ON mst_product(is_deleted);