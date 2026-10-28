; Indents query for DevxyzIDE

(xml_decl
  "<?" @indent
  "?>" @outdent)

(empty_element
  tag_name: (name) @indent
  ">" @outdent)

(end_tag_element
  (tag_start
    tag_name: (name) @indent)
  (tag_end
    "<" @outdent)
  )