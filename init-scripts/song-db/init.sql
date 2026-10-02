CREATE TABLE IF NOT EXISTS public.entity_song (
    id bigint NOT NULL,
    album character varying(100),
    artist character varying(100),
    duration character varying(5),
    name character varying(100),
    year character varying(4),
    CONSTRAINT entity_song_pkey PRIMARY KEY (id)
);