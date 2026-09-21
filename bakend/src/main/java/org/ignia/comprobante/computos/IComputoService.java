package org.ignia.comprobante.computos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IComputoService {

    List<ComputoDTO> findAll();

    Page<ComputoDTO> page(Pageable pageable);

    Page<ComputoDTO> page(String search, Pageable pageable);

    ComputoDTO save(ComputoDTO computoDTO);

    ComputoDTO update(Long id, ComputoDTO computoDTO);

    void delete(Long id);

    long count();

    ComputoDTO findById(Long id);

}
