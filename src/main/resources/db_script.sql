CREATE TABLE public.sheet
(
    id        BIGSERIAL PRIMARY KEY,
    user_id   BIGINT    NOT NULL,
    s3_bucket VARCHAR   NOT NULL,
    s3_id     VARCHAR   NOT NULL,
    updated   timestamp,
    created   timestamp NOT NULL DEFAULT now()
);

CREATE TABLE public.qr_sheet_link
(
    sheet_id    BIGINT REFERENCES public.sheet (id) ON DELETE CASCADE,
    user_id     BIGINT    NOT NULL,
    created     timestamp NOT NULL DEFAULT now(),
    expiring_at timestamp NOT NULL
);
