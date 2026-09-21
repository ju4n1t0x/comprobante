package org.ignia.comprobante.computos;

import org.ignia.comprobante.ItemComputo.ItemComputoDTO;
import org.ignia.comprobante.ItemComputo.ItemComputoModel;
import org.ignia.comprobante.productos.ProductModel;
import org.ignia.comprobante.productos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComputoService implements IComputoService{

    @Autowired
    private ComputoRepository computoRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ComputoDTO> findAll() {
        return computoRepository.findAll()
                .stream()
                .map(ComputoMapper::toComputoDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComputoDTO> page(Pageable pageable) {
        return computoRepository.findAll(pageable)
                .map(ComputoMapper::toComputoDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComputoDTO> page(String search, Pageable pageable) {
        if (search == null || search.isBlank()){
            return page(pageable);
        }
        return computoRepository.findByNameContainingIgnoreCase(search, pageable)
                .map(ComputoMapper::toComputoDto);
    }

    @Override
    @Transactional
    public ComputoDTO save(ComputoDTO computoDTO) {
        validate(computoDTO);

        ComputoModel model = ComputoModel.builder()
                .name(computoDTO.getName().trim())
                .description(computoDTO.getDescription())
                .items(new ArrayList<>())
                .build();

        replaceItems(model, computoDTO);
        return ComputoMapper.toComputoDto(computoRepository.save(model));
    }

    @Override
    @Transactional
    public ComputoDTO update(Long id, ComputoDTO computoDTO) {
        validate(computoDTO);

        ComputoModel model = computoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Computo no encontrado"));

        model.setName(computoDTO.getName().trim());
        model.setDescription(computoDTO.getDescription());
        model.getItems().clear();

        replaceItems(model, computoDTO);
        return ComputoMapper.toComputoDto(computoRepository.save(model));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!computoRepository.existsById(id)){
            throw new RuntimeException("Computo no encontrado");
        }
        computoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return computoRepository.count();
    }

    private void replaceItems(ComputoModel computoModel, ComputoDTO computoDTO){
        if (computoDTO.getItems() == null) {
            return;
        }

        for (ItemComputoDTO itemDto : computoDTO.getItems()){
            ProductModel product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            ItemComputoModel item = ItemComputoModel.builder()
                    .computo(computoModel)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .unitType(itemDto.getUnitType())
                    .build();

            computoModel.getItems().add(item);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ComputoDTO findById(Long id) {
        return computoRepository.findById(id)
                .map(ComputoMapper::toComputoDto)
                .orElseThrow(() -> new RuntimeException("Computo no encontrado"));
    }

    private void validate(ComputoDTO computoDTO){
        if (computoDTO == null || computoDTO.getName() == null || computoDTO.getName().isBlank()){
            throw new RuntimeException("El nombre del computo es obligatorio");
        }

        if (computoDTO.getItems() == null || computoDTO.getItems().isEmpty()){
            throw new RuntimeException("Debe tener al menos un item");
        }

        for (ItemComputoDTO item : computoDTO.getItems()){
            if (item.getProductId() == null){
                throw new RuntimeException("El item debe tener un producto");
            }
            if (item.getQuantity() == null){
                throw new RuntimeException("Debe ser mayor o igual a 0");
            }
            if (item.getUnitType() == null){
                throw new RuntimeException("Todos los items deben tener un tipo de unidad");
            }
        }

    }
}
