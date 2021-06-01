CREATE TABLE public.sheet(
    user_id      VARCHAR   NOT NULL PRIMARY KEY,
    sheet_id     VARCHAR   NOT NULL UNIQUE,
    content_type VARCHAR   NOT NULL,
    s3_bucket    VARCHAR   NOT NULL,
    updated      timestamp,
    created      timestamp NOT NULL DEFAULT now()
);

CREATE TABLE public.qr_sheet_link(
    qr_id       VARCHAR PRIMARY KEY,
    sheet_id    VARCHAR REFERENCES public.sheet (sheet_id) ON DELETE CASCADE,
    user_id     VARCHAR REFERENCES public.sheet (user_id) NOT NULL,
    created     timestamp                                 NOT NULL DEFAULT now(),
    expiring_at timestamp                                 NOT NULL
);

